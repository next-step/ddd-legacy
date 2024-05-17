package kitchenpos.domain.testfixture;

import kitchenpos.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderFakeRepository implements OrderRepository {

    private final InstancePocket<UUID, Order> instancePocket;

    public OrderFakeRepository() {
        this.instancePocket = new InstancePocket<>();
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable,
                                                  OrderStatus status) {
        var orders = instancePocket.findAll();
        return orders.stream()
                .anyMatch(order -> orderTable.getId().equals(order.getOrderTable().getId()) && status != order.getStatus());
    }

    @Override
    public Order save(Order order) {
        return instancePocket.save(order);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return instancePocket.findById(orderId);
    }

    @Override
    public List<Order> findAll() {
        return instancePocket.findAll();
    }
}
