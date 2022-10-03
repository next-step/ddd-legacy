package kitchenpos.domain;

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
