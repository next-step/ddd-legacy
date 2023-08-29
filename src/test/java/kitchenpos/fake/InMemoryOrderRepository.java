package kitchenpos.fake;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {
    HashMap<UUID, Order> entities = new HashMap<>();

    @Override
    public Optional<Order> findById(UUID uuid) {
        return Optional.ofNullable(entities.get(uuid));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public Order save(Order entity) {
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        for (Order order : entities.values()) {
            if (order.getOrderTable().equals(orderTable) && !order.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }
}
