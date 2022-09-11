package kitchenpos;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryOrderRepository implements OrderRepository {


    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return false;
    }

    @Override
    public Order save(Order order) {
        return null;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        return null;
    }
}
