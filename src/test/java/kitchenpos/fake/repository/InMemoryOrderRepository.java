package kitchenpos.fake.repository;

import kitchenpos.domain.*;

import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<UUID, Order> store = new HashMap<>();

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(UUID.randomUUID());
        }
        store.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return Optional.ofNullable(store.get(orderId));
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return store.values().stream()
                .anyMatch(order ->
                        Objects.equals(order.getOrderTable(), orderTable) &&
                                order.getStatus() != status
                );
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(store.values());
    }
}
