package kitchenpos.infra;

import kitchenpos.domain.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InmemoryOrderRepository implements OrderRepository {

    private final OrderIdGenerator idGenerator = UUID::randomUUID;
    private final Map<UUID, Order> store = new ConcurrentHashMap<>();

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return store.values().stream()
                .anyMatch(order -> order.getOrderTable().equals(orderTable)
                        && !order.getStatus().equals(status));
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGenerator.generateId());
        }
        store.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return Optional.ofNullable(store.get(orderId));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(store.values());
    }
}