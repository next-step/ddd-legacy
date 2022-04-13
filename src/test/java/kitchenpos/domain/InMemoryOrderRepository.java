package kitchenpos.domain;

import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {
    private Map<UUID, Order> orders = new HashMap<>();

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return orders.values()
                .stream()
                .anyMatch(order -> order.getOrderTableId().equals(orderTable.getId())
                        && order.getStatus() != status);
    }

    @Override
    public Order save(Order order) {
        orders.put(order.getId(), order);

        return order;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return orders.values()
                .stream()
                .filter(order -> Objects.equals(order.getId(), orderId))
                .findAny();
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
}
