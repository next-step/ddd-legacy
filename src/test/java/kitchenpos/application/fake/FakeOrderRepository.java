package kitchenpos.application.fake;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.*;
import java.util.stream.Collectors;

public class FakeOrderRepository implements OrderRepository {

    private final Map<UUID, Order> fakePersistence = new HashMap<>();

    @Override
    public Order save(Order order) {
        if (fakePersistence.containsKey(order.getId())) {
            throw new IllegalArgumentException("duplicate primary key");
        }
        fakePersistence.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(fakePersistence.get(id));
    }

    @Override
    public List<Order> findAll() {
        return fakePersistence.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return fakePersistence.values()
                .stream()
                .filter(order -> order.getOrderTable().equals(orderTable))
                .filter(order -> !order.getStatus().equals(status))
                .findAny()
                .isPresent();
    }
}
