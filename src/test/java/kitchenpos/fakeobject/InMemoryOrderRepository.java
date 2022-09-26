package kitchenpos.fakeobject;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.ArrayList;
import java.util.UUID;

public class InMemoryOrderRepository extends AbstractInMemoryRepository<UUID, Order> implements OrderRepository {

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return new ArrayList<>(maps.values()).stream()
                .filter(order -> orderTable.getId().equals(orderTable.getId()))
                .anyMatch(order -> order.getStatus() != status);
    }
}
