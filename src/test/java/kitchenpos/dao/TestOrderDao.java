package kitchenpos.dao;

import kitchenpos.model.Order;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class TestOrderDao implements OrderDao {

    private final Map<Long, Order> orders = new HashMap();

    @Override
    public Order save(Order entity) {
        orders.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList(orders.values());
    }

    @Override
    public boolean existsByOrderTableIdAndOrderStatusIn(Long orderTableId, List<String> orderStatuses) {
        if (Objects.isNull(orderTableId) || CollectionUtils.isEmpty(orderStatuses)) {
            return false;
        }

        return orders.values()
                .stream()
                .anyMatch(order -> orderTableId.equals(order.getOrderTableId()) && orderStatuses.contains(order.getOrderStatus()));
    }

    @Override
    public boolean existsByOrderTableIdInAndOrderStatusIn(List<Long> orderTableIds, List<String> orderStatuses) {
        if (CollectionUtils.isEmpty(orderTableIds) || CollectionUtils.isEmpty(orderStatuses)) {
            return false;
        }

        return orders.values()
                .stream()
                .anyMatch(order -> orderTableIds.contains(order.getOrderTableId()) && orderStatuses.contains(order.getOrderStatus()));
    }
}
