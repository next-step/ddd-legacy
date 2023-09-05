package kitchenpos.application.fakerepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

public class OrderFakeRepository implements OrderRepository {

    private final Map<UUID, Order> orders = new HashMap<>();


    @Override
    public Order save(final Order entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }

        orders.put(entity.getId(), entity);

        return entity;
    }

    @Override
    public Optional<Order> findById(final UUID id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findAll() {
        return orders.values()
            .stream()
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(final OrderTable orderTable,
        final OrderStatus status) {

        return orders.values()
            .stream()
            .filter(order -> order.getOrderTable().getId().equals(orderTable.getId()))
            .anyMatch(order -> !order.getStatus().equals(status));
    }
}
