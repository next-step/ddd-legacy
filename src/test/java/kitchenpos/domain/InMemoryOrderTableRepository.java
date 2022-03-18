package kitchenpos.domain;

import java.util.*;

public class InMemoryOrderTableRepository implements OrderTableRepository {
    private Map<UUID, OrderTable> orderTables = new HashMap<>();

    @Override
    public OrderTable save(OrderTable orderTable) {
        UUID id = UUID.randomUUID();
        orderTable.setId(id);
        orderTables.put(id, orderTable);

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
