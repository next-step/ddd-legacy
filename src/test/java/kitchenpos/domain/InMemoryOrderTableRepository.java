package kitchenpos.domain;

import java.util.*;

public class InMemoryOrderTableRepository implements OrderTableRepository {

    Map<UUID, OrderTable> orderTables;

    public InMemoryOrderTableRepository() {
        this.orderTables = new HashMap<>();
    }

    @Override
    public OrderTable save(OrderTable orderTable) {
        orderTable.setId(UUID.randomUUID());
        orderTables.put(orderTable.getId(), orderTable);
        return orderTable;
    }

    @Override
    public Optional<OrderTable> findById(UUID uuid) {
        return Optional.ofNullable(orderTables.get(uuid));
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(orderTables.values());
    }

}
