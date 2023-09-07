package kitchenpos.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

public class OrderFakeRepository implements OrderRepository {

    private final List<Order> orders = new ArrayList<>();

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return orders.stream()
            .anyMatch(it -> it.getOrderTableId().equals(orderTable.getId()) && it.getStatus() != status);
    }

    @Override
    public List<Order> findAll() {
        return Collections.unmodifiableList(orders);
    }

    @Override
    public Order save(Order entity) {
        orders.add(entity);
        return entity;
    }

    public Optional<Order> findById(UUID uuid) {
        return orders.stream()
            .filter(it -> uuid.equals(it.getId()))
            .findAny();
    }
}
