package kitchenpos.mock;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryOrderRepository implements OrderRepository {

    private ConcurrentMap<UUID, Order> orders = new ConcurrentHashMap<>();

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return orders.values().stream()
                .anyMatch(order ->
                        Objects.nonNull(order.getOrderTable()) &&
                        Objects.equals(order.getOrderTable().getId(), orderTable.getId())
                                && !Objects.equals(order.getStatus(), status));
    }

    @Override
    public Order save(Order order) {
        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
}
