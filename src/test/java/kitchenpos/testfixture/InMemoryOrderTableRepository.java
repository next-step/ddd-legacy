package kitchenpos.testfixture;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;

public class InMemoryOrderTableRepository implements OrderTableRepository {
    private final Map<UUID, OrderTable> orderTables = new HashMap<>();

    @Override
    public OrderTable save(OrderTable entity) {
        orderTables.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return Optional.ofNullable(orderTables.get(id));
    }

    @Override
    public List<OrderTable> findAll() {
        return orderTables.values().stream().toList();
    }
}
