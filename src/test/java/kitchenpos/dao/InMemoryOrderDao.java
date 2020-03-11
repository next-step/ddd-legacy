package kitchenpos.dao;

import kitchenpos.model.Order;
import kitchenpos.support.OrderBuilder;

import java.util.*;

public class InMemoryOrderDao implements OrderDao {

    private final Map<Long, Order> entities = new HashMap<>();

    @Override
    public Order save(Order entity) {
        Order savedOrder = new OrderBuilder()
            .id(entity.getId())
            .orderTableId(entity.getOrderTableId())
            .orderStatus(entity.getOrderStatus())
            .orderedTime(entity.getOrderedTime())
            .orderLineItems(entity.getOrderLineItems() == null?
                new ArrayList<>() : new ArrayList<>(entity.getOrderLineItems()))
            .build();

        entities.put(entity.getId(), savedOrder);
        return savedOrder;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public boolean existsByOrderTableIdAndOrderStatusIn(Long orderTableId, List<String> orderStatuses) {
        return this.findAll().stream()
            .filter(order -> order.getOrderTableId() == orderTableId)
            .map(Order::getOrderStatus)
            .anyMatch(status -> orderStatuses.contains(status));
    }

    @Override
    public boolean existsByOrderTableIdInAndOrderStatusIn(List<Long> orderTableIds, List<String> orderStatuses) {
        return findAll().stream()
            .filter(order -> orderTableIds.contains(order.getOrderTableId()))
            .map(Order::getOrderStatus)
            .anyMatch(status -> orderStatuses.contains(status));
    }
}
