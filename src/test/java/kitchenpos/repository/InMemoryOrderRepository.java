package kitchenpos.repository;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryOrderRepository implements OrderRepository {
    private final BaseInMemoryDao<Order> dao = new BaseInMemoryDao<>();


    @Override
    public Order save(Order order) {
        OrderTable orderTable = order.getOrderTable();
        if (orderTable != null) {
            order.setOrderTableId(orderTable.getId());
        }
        return dao.save(order);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return dao.findById(id);
    }

    @Override
    public List<Order> findAll() {
        return dao.findAll();
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return dao.getAll().stream()
                .anyMatch(order -> order.getOrderTable().equals(orderTable) && !order.getStatus().equals(status));
    }
}
