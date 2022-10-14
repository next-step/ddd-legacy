package kitchenpos.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeOrderTableRepository implements OrderTableRepository {

    private final Map<UUID, OrderTable> orderTables = new HashMap<>();

    @Override
    public OrderTable save(OrderTable orderTable) {
        this.orderTables.put(orderTable.getId(), orderTable);
        return orderTable;
    }

    @Override
    public List<OrderTable> findAll() {
        final Collection<OrderTable> results = this.orderTables.values();
        return List.copyOf(results);
    }

    @Override
    public Optional<OrderTable> findById(UUID id) {
        OrderTable result = this.orderTables.get(id);
        return Optional.ofNullable(result);
    }
}
