package kitchenpos.application;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

public class InMemoryOrderRepository implements OrderRepository {

    private final Map<UUID, Order> orders = new LinkedHashMap<>();

    @Override
    public Order save(final Order order) {
        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(final UUID id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(final OrderTable orderTable, final OrderStatus status) {
        return orders.values()
            .stream()
            .anyMatch(order -> orders.containsKey(orderTable.getId()) && !Objects.equals(order.getStatus(), status));
    }
}
