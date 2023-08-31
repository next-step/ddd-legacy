package kitchenpos.domain;

import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {

    private final Map<UUID, Order> map = new HashMap<>();

    @Override
    public Order save(Order entity) {
        map.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Order> findById(UUID uuid) {
        return Optional.ofNullable(map.get(uuid));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return map.values().stream()
                .filter(it -> it.getOrderTable().getId().equals(orderTable.getId()))
                .anyMatch(it ->  it.getStatus() != status);
    }
}
