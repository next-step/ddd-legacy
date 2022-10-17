package kitchenpos.application.fake;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.*;

public class FakeOrderRepository implements OrderRepository {

    private final Map<UUID, Order> orderMap = new HashMap<>();

    @Override
    public Order save(Order order) {
        UUID uuid = UUID.randomUUID();
        order.setId(uuid);
        orderMap.put(uuid, order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(orderMap.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orderMap.values());
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        for (Order order : orderMap.values()) {
            if (order.getOrderTable().equals(orderTable) && !order.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }

}
