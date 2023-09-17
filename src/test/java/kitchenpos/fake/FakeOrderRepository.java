package kitchenpos.fake;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeOrderRepository implements OrderRepository {

    private List<Order> orders = new ArrayList<>();

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return orders.stream()
                .noneMatch(order -> order.getStatus().equals(status)
                && order.getOrderTable().getId().equals(orderTable.getId()));
    }

    @Override
    public Order save(Order order) {
        orders.add(order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return orders.stream()
                .filter(order -> order.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Order> findAll() {
        return orders;
    }
}
