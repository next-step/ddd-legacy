package kitchenpos.mock.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

public class FakeOrderRepository implements OrderRepository {

    private final Map<UUID, Order> dataMap = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(UUID.randomUUID());
        }
        UUID id = order.getId();
        dataMap.put(id, order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return dataMap.values()
            .stream()
            .filter(order -> order.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(dataMap.values());
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return dataMap.values().stream()
            .anyMatch(order ->
                order.getOrderTable() != null
                    && order.getOrderTable().getId().equals(orderTable.getId())
                    && order.getStatus() != status
            );
    }
}
