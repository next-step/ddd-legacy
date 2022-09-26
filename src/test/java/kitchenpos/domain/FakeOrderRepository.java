package kitchenpos.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class FakeOrderRepository implements OrderRepository {

    private final Map<UUID, Order> orders = new HashMap<>();

    @Override
    public Order save(Order order) {
        this.orders.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        final Order result = this.orders.get(id);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Order> findAll() {
        final Collection<Order> results = this.orders.values();
        return List.copyOf(results);
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return this.orders.values()
                .stream()
                .anyMatch(order -> (
                        (order.getOrderTableId() == orderTable.getId())
                                && (order.getStatus() != status)
                ));
    }
}
