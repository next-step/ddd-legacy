package kitchenpos.testfixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<UUID, Order> orders = new HashMap<>();

    @Override
    public Order save(Order entity) {
        orders.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findAll() {
        return orders.values().stream().toList();
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        List<Order> exists = orders.values()
                .stream()
                .filter(order -> order.getOrderTableId().equals(orderTable.getId()))
                .toList();

        if(exists.isEmpty()){
            return false;
        }

        boolean isMatched = exists
                .stream()
                .anyMatch(order -> order.getStatus().equals(status));

        if(isMatched){
            return false;
        }

        return true;
    }
}
