package kitchenpos.bo.mock;

import kitchenpos.dao.OrderDao;
import kitchenpos.model.Order;

import java.util.*;
import java.util.stream.Collectors;

public class TestOrderDao implements OrderDao {

    private static final Map<Long, Order> data = new HashMap<>();

    @Override
    public Order save(Order entity) {
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        return data.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByOrderTableIdInAndOrderStatusIn(List<Long> orderTableIds, List<String> orderStatuses) {
        return orderTableIds.stream()
                .filter(id -> Objects.nonNull(data.get(id)))
                .map(id -> data.get(id).getOrderStatus())
                .anyMatch(orderStatus -> orderStatuses.contains(orderStatus));
    }

    @Override
    public boolean existsByOrderTableIdAndOrderStatusIn(Long orderTableId, List<String> orderStatuses) {
        return data.values()
                .stream()
                .filter(order -> order.getOrderTableId() == orderTableId)
                .anyMatch(order -> orderStatuses.contains(order.getOrderStatus()));
    }
}
