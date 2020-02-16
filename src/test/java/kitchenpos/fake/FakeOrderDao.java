package kitchenpos.fake;

import kitchenpos.dao.OrderDao;
import kitchenpos.model.Order;

import java.util.*;

public class FakeOrderDao implements OrderDao {
    Map<Long, Order> values = new HashMap<>();

    @Override
    public Order save(Order entity) {
        values.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(values.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(values.values());
    }

    @Override
    public boolean existsByOrderTableIdAndOrderStatusIn(Long orderTableId, List<String> orderStatuses) {
        return values.values()
                .stream()
                .anyMatch(order -> order.getOrderTableId().equals(orderTableId) && orderStatuses.contains(order.getOrderStatus()));
    }

    @Override
    public boolean existsByOrderTableIdInAndOrderStatusIn(List<Long> orderTableIds, List<String> orderStatuses) {
        return values.values()
                .stream()
                .anyMatch(order -> orderTableIds.contains(order.getOrderTableId()) && orderStatuses.contains(order.getOrderStatus()));
    }
}
