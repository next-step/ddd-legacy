package kitchenpos.infra;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;

public class InMemoryOrderTableRepository implements OrderTableRepository {

    private final Map<UUID, OrderTable> orderTables = new HashMap<>();

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
