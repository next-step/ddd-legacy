package kitchenpos.application.fake.helper;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {

    private final Map<UUID, Order> elements = new HashMap<>();

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return elements.values().stream()
                .filter(it -> Objects.equals(it.getOrderTableId(), orderTable.getId()))
                .noneMatch(it -> it.getStatus().equals(status));
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(elements.get(id));
    }

    @Override
    public Order save(Order order) {
        elements.put(order.getId(), order);
        return order;
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(elements.values());
    }
}
