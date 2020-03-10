package kitchenpos.fake;

import kitchenpos.dao.OrderDao;
import kitchenpos.model.Order;

import java.util.*;

public class FakeOrderDao implements OrderDao {
    private Map<Long, Order> entities = new HashMap<>();

    @Override
    public Order save(Order entity) {
        entities.put(entity.getId(), entity);
        return entity;
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
        return entities.values()
                .stream()
                .anyMatch(order -> order.getOrderTableId().equals(orderTableId) && orderStatuses.contains(order.getOrderStatus()));
    }

    @Override
    public boolean existsByOrderTableIdInAndOrderStatusIn(List<Long> orderTableIds, List<String> orderStatuses) {
        return entities.values()
                .stream()
                .anyMatch(order -> orderTableIds.contains(order.getOrderTableId()) && orderStatuses.contains(order.getOrderStatus()));
    }
}
