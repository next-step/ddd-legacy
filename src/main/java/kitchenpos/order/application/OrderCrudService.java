package kitchenpos.order.application;

import kitchenpos.common.vo.Quantity;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.order.domain.*;
import kitchenpos.order.dto.request.OrderLineItemRequest;
import kitchenpos.order.dto.request.OrderRequest;
import kitchenpos.order.vo.DeliveryAddress;
import kitchenpos.ordertable.domain.OrderTable;
import kitchenpos.ordertable.domain.OrderTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderCrudService {
    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final OrderTableRepository orderTableRepository;

    public OrderCrudService(
            final OrderRepository orderRepository,
            final MenuRepository menuRepository,
            final OrderTableRepository orderTableRepository
    ) {
        this.orderRepository = orderRepository;
        this.menuRepository = menuRepository;
        this.orderTableRepository = orderTableRepository;
    }

    @Transactional
    public Order create(final OrderRequest request) {
        final OrderType type = request.getType();
        final List<OrderLineItemRequest> orderLineItemRequests = request.getOrderLineItems();
        validateMenuSize(orderLineItemRequests);
        final List<OrderLineItem> orderLineItems = new ArrayList<>();
        for (final OrderLineItemRequest orderLineItemRequest : orderLineItemRequests) {
            final long quantity = orderLineItemRequest.getQuantity();
            final Menu menu = menuRepository.findById(orderLineItemRequest.getMenuId())
                    .orElseThrow(NoSuchElementException::new);
            validatePrice(orderLineItemRequest, menu.getPrice());
            orderLineItems.add(new OrderLineItem(menu, new Quantity(quantity)));
        }
        OrderTable orderTable = null;
        if (type == OrderType.EAT_IN) {
            orderTable = orderTableRepository.findById(request.getOrderTableId())
                    .orElseThrow(NoSuchElementException::new);
        }
        DeliveryAddress deliveryAddress = null;
        if (type == OrderType.DELIVERY) {
            deliveryAddress = new DeliveryAddress(request.getDeliveryAddress());
        }
        Order order = new Order(UUID.randomUUID(), type, orderLineItems, orderTable, deliveryAddress);
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(orderLineItems);
        return orderRepository.save(order);
    }

    private static void validatePrice(OrderLineItemRequest orderLineItemRequest, BigDecimal menuPrice) {
        if (menuPrice.compareTo(orderLineItemRequest.getPrice()) != 0) {
            throw new IllegalArgumentException("메뉴의 가격과 메뉴 항목의 가격은 같다.");
        }
    }

    private void validateMenuSize(List<OrderLineItemRequest> orderLineItemRequests) {
        final List<Menu> menus = menuRepository.findAllByIdIn(
                orderLineItemRequests.stream()
                        .map(OrderLineItemRequest::getMenuId)
                        .collect(Collectors.toList())
        );
        if (menus.size() != orderLineItemRequests.size()) {
            throw new IllegalArgumentException("메뉴의 수량과 주문 항목의 수량은 같다.");
        }
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
