package kitchenpos.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeOrderRepository implements OrderRepository {

    private final Map<UUID, Order> elements = new HashMap<>();

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return false;
    }

    @Override
    public Order save(Order order) {
        final UUID orderId = order.getId();
        elements.put(orderId, order);
        return elements.get(orderId);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(elements.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(elements.values());
    }
}
