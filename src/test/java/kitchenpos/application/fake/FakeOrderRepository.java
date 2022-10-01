package kitchenpos.application.fake;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import org.assertj.core.util.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeOrderRepository implements OrderRepository {

    private final Map<UUID, Order> memoryMap = new HashMap<>();


    @Override
    public Order save(Order order) {
        memoryMap.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return Optional.ofNullable(memoryMap.get(orderId));
    }

    @Override
    public List<Order> findAll() {
        return Lists.newArrayList(memoryMap.values());
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return Lists.newArrayList(memoryMap.values())
                .stream()
                .filter(order ->  order.getOrderTable().getId() == orderTable.getId())
                .anyMatch(order -> order.getStatus() == order.getStatus());
    }
}
