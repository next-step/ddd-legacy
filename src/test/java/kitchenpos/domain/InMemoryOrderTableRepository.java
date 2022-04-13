package kitchenpos.domain;

import java.util.*;

public class InMemoryOrderTableRepository implements OrderTableRepository {
    private Map<UUID, OrderTable> orderTables = new HashMap<>();

    @Override
    public OrderTable save(OrderTable orderTable) {
        orderTables.put(orderTable.getId(), orderTable);

        return orderTable;
    }

    @Override
    public Optional<OrderTable> findById(UUID orderTableId) {
        return orderTables.values()
                .stream()
                .filter(orderTable -> Objects.equals(orderTable.getId(), orderTableId))
                .findAny();
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(orderTables.values());
    }
}
