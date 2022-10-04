package kitchenpos.fake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import org.springframework.util.ObjectUtils;

public class InMemoryOrderRepository implements OrderRepository {

    private Map<UUID, Order> orders = new HashMap<>();

    @Override
    public boolean existsByOrderTableAndStatusNot(final OrderTable orderTable, OrderStatus status) {
        return orders.values().stream()
                .anyMatch((order) -> !ObjectUtils.isEmpty(order.getOrderTable()) && !order.getStatus().equals(status));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public Order save(Order order) {
        order.setId(UUID.randomUUID());
        orders.put(order.getId(), order);
        return orders.get(order.getId());
    }
}
