package kitchenpos.application.fake;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.*;
import java.util.stream.Collectors;

public class FakeOrderRepository implements OrderRepository {
    Map<UUID, Order> memory = new HashMap<>();
    @Override
    public Order save(Order order) {
        memory.put(order.getId(), order);
        return memory.get(order.getId());
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(memory.get(id));
    }

    @Override
    public List<Order> findAll() {
        return memory.values().stream().collect(Collectors.toList());
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status){
        return memory.values()
                .stream()
                .anyMatch(order -> order.getOrderTable().equals(orderTable) && order.getStatus().equals(status));
    }
}
