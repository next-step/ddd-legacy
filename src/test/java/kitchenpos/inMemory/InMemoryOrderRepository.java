package kitchenpos.inMemory;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {

    Map<UUID, Order> orders = new HashMap<>();

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        if (orderTable.getNumberOfGuests() != 0 || !orderTable.isEmpty())
            return true;
        if (!OrderStatus.COMPLETED.equals(status)) {
            return true;
        }
        return false;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return orders.values()
                .stream()
                .filter(it -> Objects.equals(it.getId(), orderId))
                .findAny();
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public Order save(Order order) {
        orders.put(order.getId(), order);
        return order;
    }
}
