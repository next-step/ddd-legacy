package kitchenpos.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryOrderTableRepository implements OrderTableRepository {

    private Map<UUID, OrderTable> orderTables = new HashMap<>();

    @Override
    public OrderTable save(final OrderTable orderTable) {
        orderTables.put(orderTable.getId(), orderTable);
        return orderTable;
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(orderTables.values());
    }

    @Override
    public Optional<OrderTable> findById(final UUID id) {
        return Optional.ofNullable(orderTables.get(id));
    }
}
