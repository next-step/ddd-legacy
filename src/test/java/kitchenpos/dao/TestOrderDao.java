package kitchenpos.dao;

import kitchenpos.model.Order;
import kitchenpos.model.OrderBuilder;

import java.util.*;

public class TestOrderDao implements OrderDao {

    private Map<Long, Order> orders = new HashMap<>();

    @Override
    public Order save(Order entity) {
        Objects.requireNonNull(entity);
        Order newOrder = OrderBuilder
                .anOrder()
                .withId(new Random().nextLong())  //id random 생성
                .withOrderLineItems(entity.getOrderLineItems())
                .withOrderStatus(entity.getOrderStatus())
                .withOrderTableId(entity.getOrderTableId())
                .withOrderedTime(entity.getOrderedTime())
                .build();

        orders.put(newOrder.getId(), newOrder);

        return newOrder;
    }

    @Override
    public Optional<Order> findById(Long id) {
        Objects.requireNonNull(id);

        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public boolean existsByOrderTableIdAndOrderStatusIn(Long orderTableId, List<String> orderStatuses) {
        Objects.requireNonNull(orderTableId);
        Objects.requireNonNull(orderStatuses);

        return orders.values()
                .stream()
                .anyMatch(i -> i.getOrderTableId().equals(orderTableId)
                        && orderStatuses.contains(i.getOrderStatus())
                );
    }

    @Override
    public boolean existsByOrderTableIdInAndOrderStatusIn(List<Long> orderTableIds, List<String> orderStatuses) {
        Objects.requireNonNull(orderTableIds);
        Objects.requireNonNull(orderStatuses);

        return orders.values()
                .stream()
                .anyMatch(i -> orderTableIds.contains(i.getOrderTableId())
                        && orderStatuses.contains(i.getOrderStatus())
                );
    }
}
