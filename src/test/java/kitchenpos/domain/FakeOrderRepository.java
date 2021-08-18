package kitchenpos.domain;

import java.util.*;

public class FakeOrderRepository implements OrderRepository {
    Map<UUID, Order> orderMap = new LinkedHashMap<>();

    @Override
    public Order save(final Order order) {
        orderMap.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(final UUID orderId) {
        return Optional.ofNullable(orderMap.get(orderId));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orderMap.values());
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(final OrderTable orderTable, final OrderStatus status) {
        return orderMap.values().stream()
                .anyMatch(order -> order.getOrderTable().getId().equals(orderTable.getId())
                        && !order.getStatus().equals(status));
    }
}
