package kitchenpos.application;

import kitchenpos.application.Exception.*;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import static kitchenpos.application.Exception.ErrorCode.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final OrderTableRepository orderTableRepository;
    private final KitchenridersClient kitchenridersClient;

    public OrderService(
        final OrderRepository orderRepository,
        final MenuRepository menuRepository,
        final OrderTableRepository orderTableRepository,
        final KitchenridersClient kitchenridersClient
    ) {
        this.orderRepository = orderRepository;
        this.menuRepository = menuRepository;
        this.orderTableRepository = orderTableRepository;
        this.kitchenridersClient = kitchenridersClient;
    }

    @Transactional
    public Order create(final Order request) throws OrderException {
        final OrderType type = request.getType();
        if (Objects.isNull(type)) {
            throw new OrderException(ORDER_TYPE_INVALID);
        }
        final List<OrderLineItem> orderLineItemRequests = request.getOrderLineItems();
        if (Objects.isNull(orderLineItemRequests) || orderLineItemRequests.isEmpty()) {
            throw new OrderException(ORDER_LINE_ITEM_NOT_FOUND);
        }
        final List<Menu> menus = menuRepository.findAllByIdIn(
            orderLineItemRequests.stream()
                .map(OrderLineItem::getMenuId)
                .toList()
        );
        if (menus.size() != orderLineItemRequests.size()) {
            throw new OrderException(ORDER_LINE_ITEM_SIZE_NOT_MATCHED);
        }
        final List<OrderLineItem> orderLineItems = new ArrayList<>();
        for (final OrderLineItem orderLineItemRequest : orderLineItemRequests) {
            final long quantity = orderLineItemRequest.getQuantity();
            if (type != OrderType.EAT_IN) {
                if (quantity < 0) {
                    throw new OrderException(ORDER_LINE_QUANTITY_NEGATIVE);
                }
            }
            final Menu menu = menuRepository.findById(orderLineItemRequest.getMenuId())
                .orElseThrow(MenuNotFoundException::new);
            if (!menu.isDisplayed()) {
                throw new OrderException(MENU_DISPLAY_FALSE);
            }
            if (menu.getPrice().compareTo(orderLineItemRequest.getPrice()) != 0) {
                throw new OrderException(MENU_PRICE_INVALID);
            }
            final OrderLineItem orderLineItem = new OrderLineItem();
            orderLineItem.setMenu(menu);
            orderLineItem.setQuantity(quantity);
            orderLineItems.add(orderLineItem);
        }
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(orderLineItems);
        if (type == OrderType.DELIVERY) {
            final String deliveryAddress = request.getDeliveryAddress();
            if (Objects.isNull(deliveryAddress) || deliveryAddress.isEmpty()) {
                throw new OrderException(DELIVERY_ADDRESS_NOT_FOUND);
            }
            order.setDeliveryAddress(deliveryAddress);
        }
        if (type == OrderType.EAT_IN) {
            final OrderTable orderTable = orderTableRepository.findById(request.getOrderTableId())
                .orElseThrow(() -> new OrderException(ORDER_TABLE_NOT_FOUND));
            if (!orderTable.isOccupied()) {
                throw new OrderException(ORDER_TABLE_EMPTY);
            }
            order.setOrderTable(orderTable);
        }
        return orderRepository.save(order);
    }

    @Transactional
    public Order accept(final UUID orderId) {
        final Order order = orderRepository.findById(orderId)
            .orElseThrow(OrderNotFoundException::new);
        if (order.getStatus() != OrderStatus.WAITING) {
            throw new OrderException(ORDER_STATUS_INVALID);
        }
        if (order.getType() == OrderType.DELIVERY) {
            BigDecimal sum = BigDecimal.ZERO;
            for (final OrderLineItem orderLineItem : order.getOrderLineItems()) {
                sum = orderLineItem.getMenu()
                    .getPrice()
                    .multiply(BigDecimal.valueOf(orderLineItem.getQuantity()));
            }
            kitchenridersClient.requestDelivery(orderId, sum, order.getDeliveryAddress());
        }
        order.setStatus(OrderStatus.ACCEPTED);
        return order;
    }

    @Transactional
    public Order serve(final UUID orderId) {
        final Order order = orderRepository.findById(orderId)
            .orElseThrow(NoSuchElementException::new);
        if (order.getStatus() != OrderStatus.ACCEPTED) {
            throw new OrderException(ORDER_STATUS_INVALID);
        }
        order.setStatus(OrderStatus.SERVED);
        return order;
    }

    @Transactional
    public Order startDelivery(final UUID orderId) {
        final Order order = orderRepository.findById(orderId)
            .orElseThrow(OrderNotFoundException::new);
        if (order.getType() != OrderType.DELIVERY) {
            throw new OrderException(ORDER_TYPE_INVALID);
        }
        if (order.getStatus() != OrderStatus.SERVED) {
            throw new OrderException(ORDER_STATUS_INVALID);
        }
        order.setStatus(OrderStatus.DELIVERING);
        return order;
    }

    @Transactional
    public Order completeDelivery(final UUID orderId) {
        final Order order = orderRepository.findById(orderId)
            .orElseThrow(NoSuchElementException::new);
        if (order.getStatus() != OrderStatus.DELIVERING) {
            throw new IllegalStateException();
        }
        order.setStatus(OrderStatus.DELIVERED);
        return order;
    }

    @Transactional
    public Order complete(final UUID orderId) {
        final Order order = orderRepository.findById(orderId)
            .orElseThrow(NoSuchElementException::new);
        final OrderType type = order.getType();
        final OrderStatus status = order.getStatus();
        if (type == OrderType.DELIVERY) {
            if (status != OrderStatus.DELIVERED) {
                throw new OrderException(ORDER_NOT_DELIVERED_YET);
            }
        }
        if (type == OrderType.TAKEOUT || type == OrderType.EAT_IN) {
            if (status != OrderStatus.SERVED) {
                throw new OrderException(ORDER_NOT_SERVED_YET);
            }
        }
        order.setStatus(OrderStatus.COMPLETED);
        if (type == OrderType.EAT_IN) {
            final OrderTable orderTable = order.getOrderTable();
            if (!orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)) {
                orderTable.setNumberOfGuests(0);
                orderTable.setOccupied(false);
            }
        }
        return order;
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
