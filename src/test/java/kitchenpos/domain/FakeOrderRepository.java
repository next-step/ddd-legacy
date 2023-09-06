package kitchenpos.domain;

import java.util.*;

public class FakeOrderRepository implements OrderRepository {
    private final Map<UUID, Order> orders = new HashMap<>();


    @Override
    public Order save(Order order) {
        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return orders.values().stream()
                .anyMatch(order -> order.getOrderTable().equals(orderTable) && !order.getStatus().equals(status));
    }
}
