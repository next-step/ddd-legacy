package kitchenpos.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

public class InMemoryOrderRepository implements OrderRepository {

    private final Map<UUID, Order> orders = new HashMap<>();

    @Override
    public Optional<Order> findById(UUID orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return orders.values().stream()
            .anyMatch(
                order -> order.getOrderTableId().equals(orderTable.getId()) && !order.getStatus().equals(status));
    }

    @Override
    public Order save(Order order) {
        order.setId(UUID.randomUUID());
        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
}
