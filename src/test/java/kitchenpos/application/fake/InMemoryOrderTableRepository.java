package kitchenpos.application.fake;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOrderTableRepository implements OrderTableRepository {
    private Map<UUID, OrderTable> orderTables = new ConcurrentHashMap<>();

    @Override
    public OrderTable save(OrderTable orderTable) {
        orderTables.put(orderTable.getId(), orderTable);
        return orderTable;
    }

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return Optional.ofNullable(orderTables.get(id));
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(orderTables.values());
    }
}
