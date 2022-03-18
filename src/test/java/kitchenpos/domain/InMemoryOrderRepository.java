package kitchenpos.domain;

import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {
    private Map<UUID, Order> orders = new HashMap<>();

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return orders.values()
                .stream()
                .filter(order -> Objects.equals(order.getOrderTableId(), orderTable.getId()))
                .filter(order -> !Objects.equals(order.getStatus(), status))
                .findAny()
                .isPresent();
    }

    @Override
    public Order save(Order order) {
        UUID id = UUID.randomUUID();
        order.setId(id);
        orders.put(id, order);

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
