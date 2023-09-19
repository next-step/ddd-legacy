package kitchenpos.fake;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.*;
import java.util.stream.Collectors;

public class FakeOrderRepository implements OrderRepository {

    private Map<UUID, Order> orders = new HashMap<>();

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return orders.values()
                .stream()
                .noneMatch(order -> order.getStatus().equals(status)
                && order.getOrderTable().getId().equals(orderTable.getId()));
    }

    @Override
    public Order save(Order order) {
        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findAll() {
        return orders.values()
                .stream()
                .collect(Collectors.toList());
    }
}
