package kitchenpos.order.application;

import kitchenpos.common.infra.KitchenridersClient;
import kitchenpos.order.domain.*;
import kitchenpos.ordertable.domain.OrderTable;
import kitchenpos.ordertable.vo.NumberOfGuests;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class OrderStatusService {
    private final OrderRepository orderRepository;
    private final KitchenridersClient kitchenridersClient;

    public OrderStatusService(
            final OrderRepository orderRepository,
            final KitchenridersClient kitchenridersClient
    ) {
        this.orderRepository = orderRepository;
        this.kitchenridersClient = kitchenridersClient;
    }

    @Transactional
    public Order accept(final UUID orderId) {
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(NoSuchElementException::new);
        order.accept();
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
        order.served();
        return order;
    }

    @Transactional
    public Order startDelivery(final UUID orderId) {
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(NoSuchElementException::new);
        if (order.getType() != OrderType.DELIVERY) {
            throw new IllegalStateException();
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
                throw new IllegalStateException();
            }
        }
        if (type == OrderType.TAKEOUT || type == OrderType.EAT_IN) {
            if (status != OrderStatus.SERVED) {
                throw new IllegalStateException();
            }
        }
        order.setStatus(OrderStatus.COMPLETED);
        if (type == OrderType.EAT_IN) {
            final OrderTable orderTable = order.getOrderTable();
            if (!orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)) {
                orderTable.changeNumberOfGuests(new NumberOfGuests(0));
                orderTable.setOccupied(false);
            }
        }
        return order;
    }
}
