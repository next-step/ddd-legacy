package kitchenpos.inMemory;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;

public class InMemoryOrderTableRepository implements OrderTableRepository {

    Map<UUID, OrderTable> orderTables = new HashMap<>();

    @Override
    public OrderTable save(OrderTable orderTable) {
        orderTables.put(orderTable.getId(), orderTable);
        return orderTable;
    }

    @Override
    public Optional<OrderTable> findById(UUID orderTableId) {
        return orderTables.values()
                .stream()
                .filter(it -> Objects.equals(it.getId(), orderTableId))
                .findAny();
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(orderTables.values());
    }
}
