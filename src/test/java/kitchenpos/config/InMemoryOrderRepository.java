package kitchenpos.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

public class InMemoryOrderRepository implements OrderRepository {

    private final HashMap<UUID, Order> orders = new HashMap<>();

    @Override
    public Order save(Order entity) {
        orders.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return orders.values().stream()
                .anyMatch(order -> isEqualOrderTable(order, orderTable)
                        && !isStatus(order, status));
    }

    private boolean isEqualOrderTable(Order order, OrderTable orderTable) {
        return order.getOrderTable().getId().equals(orderTable.getId());
    }

    private boolean isStatus(Order order, OrderStatus status) {
        return order.getStatus().equals(status);
    }
}
