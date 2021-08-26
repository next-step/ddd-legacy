package kitchenpos.domain;

import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {

    Map<UUID, Order> orders;

    public InMemoryOrderRepository() {
        orders = new HashMap<>();
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(UUID.randomUUID());
            orders.put(order.getId(), order);
            return order;
        }

        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID uuid) {
        return Optional.ofNullable(orders.get(uuid));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return false;
    }

}
