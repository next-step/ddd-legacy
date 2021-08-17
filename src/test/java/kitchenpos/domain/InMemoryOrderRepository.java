package kitchenpos.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InMemoryOrderRepository implements OrderRepository {

    private Map<UUID, Order> orders = new HashMap<>();

    @Override
    public boolean existsByOrderTableAndStatusNot(
        final OrderTable orderTable,
        final OrderStatus status
    ) {
        return orders.values()
            .stream()
            .noneMatch(order ->
                Objects.equals(orderTable.getId(), order.getOrderTable().getId())
                    && order.getStatus().equals(status)
            );
    }

    @Override
    public Order save(Order order) {
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
}
