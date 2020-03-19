package kitchenpos.dao;

import kitchenpos.model.Order;

import java.util.*;

public class InMemoryOrderDao implements OrderDao {
    private final Map<Long, Order> data = new HashMap<>();

    @Override
    public Order save(Order entity) {
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean existsByOrderTableIdAndOrderStatusIn(Long orderTableId, List<String> orderStatuses) {
        return data.values().stream()
                .anyMatch(order ->
                        orderStatuses.contains(order.getOrderStatus())
                                && orderTableId.equals(order.getOrderTableId()));
    }

    @Override
    public boolean existsByOrderTableIdInAndOrderStatusIn(List<Long> orderTableIds, List<String> orderStatuses) {
        return data.values().stream()
                .anyMatch(order ->
                        orderStatuses.contains(order.getOrderStatus())
                                && orderTableIds.contains(order.getOrderTableId()));
    }
}
