package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    public Order create(final Order request) {
        final OrderType type = request.getType();
        if (Objects.isNull(type)) {
            throw new IllegalArgumentException("주문할 수 없는 주문 타입입니다.");
        }
        final List<OrderLineItem> orderLineItemRequests = request.getOrderLineItems();
        if (Objects.isNull(orderLineItemRequests) || orderLineItemRequests.isEmpty()) {
            throw new IllegalArgumentException("주문 항목이 존재하지 않습니다.");
        }
        final List<Menu> menus = menuRepository.findAllByIdIn(
            orderLineItemRequests.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList())
        );
        if (menus.size() != orderLineItemRequests.size()) {
            throw new IllegalArgumentException("주문 항목과 메뉴가 일치하지 않습니다.");
        }
        final List<OrderLineItem> orderLineItems = new ArrayList<>();
        for (final OrderLineItem orderLineItemRequest : orderLineItemRequests) {
            final long quantity = orderLineItemRequest.getQuantity();
            if (type != OrderType.EAT_IN) {
                if (quantity < 0) {
                    throw new IllegalArgumentException("주문 수량은 0 이상이어야 합니다.");
                }
            }
            final Menu menu = menuRepository.findById(orderLineItemRequest.getMenuId())
                .orElseThrow(NoSuchElementException::new);
            if (!menu.isDisplayed()) {
                throw new IllegalStateException("메뉴가 노출되지 않았습니다.");
            }
            if (menu.getPrice().compareTo(orderLineItemRequest.getPrice()) != 0) {
                throw new IllegalArgumentException("주문 항목 가격과 메뉴 가격이 일치하지 않습니다.");
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
                throw new IllegalArgumentException("배달 주소가 존재하지 않습니다.");
            }
            order.setDeliveryAddress(deliveryAddress);
        }
        if (type == OrderType.EAT_IN) {
            final OrderTable orderTable = orderTableRepository.findById(request.getOrderTableId())
                .orElseThrow(NoSuchElementException::new);
            if (!orderTable.isOccupied()) {
                throw new IllegalStateException("테이블이 비어있습니다.");
            }
            order.setOrderTable(orderTable);
        }
        return orderRepository.save(order);
    }

    @Transactional
    public Order accept(final UUID orderId) {
        final Order order = orderRepository.findById(orderId)
            .orElseThrow(NoSuchElementException::new);
        if (order.getStatus() != OrderStatus.WAITING) {
            throw new IllegalStateException("주문 대기 상태가 아닙니다.");
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
            throw new IllegalStateException("주문 대기 상태가 아닙니다.");
        }
        order.setStatus(OrderStatus.SERVED);
        return order;
    }

    @Transactional
    public Order startDelivery(final UUID orderId) {
        final Order order = orderRepository.findById(orderId)
            .orElseThrow(NoSuchElementException::new);
        if (order.getType() != OrderType.DELIVERY) {
            throw new IllegalStateException("배달 주문이 아닙니다.");
        }
        if (order.getStatus() != OrderStatus.SERVED) {
            throw new IllegalStateException();
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
                throw new IllegalStateException("배달이 완료되지 않았습니다.");
            }
        }
        if (type == OrderType.TAKEOUT || type == OrderType.EAT_IN) {
            if (status != OrderStatus.SERVED) {
                throw new IllegalStateException("주문이 제공되지 않았습니다.");
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
