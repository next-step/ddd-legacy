package kitchenpos.fake.order;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TestOrderRepository implements OrderRepository {
    private final ConcurrentHashMap<UUID, Order> orders = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        Optional<Order> first = orders.values()
                                      .stream()
                                      .filter(o -> o.getOrderTable().getId().equals(orderTable.getId()) && o.getStatus() != status)
                                      .findFirst();
        return first.isPresent();
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
}
