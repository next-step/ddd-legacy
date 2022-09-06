package kitchenpos.integration.mock;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MemoryOrderRepository implements OrderRepository {
    private static final Map<UUID, Order> STORE = new HashMap<>();

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return STORE.values()
                .stream()
                .filter(it -> it.getOrderTable().equals(orderTable))
                .anyMatch(it -> !it.getStatus().equals(status));
    }

    @Override
    public Order save(Order order) {
        STORE.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        if (!STORE.containsKey(id)) {
            return Optional.empty();
        }

        return Optional.of(STORE.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(STORE.values());
    }

    public void clear() {
        STORE.clear();
    }
}
