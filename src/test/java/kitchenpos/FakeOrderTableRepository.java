package kitchenpos;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;

public class FakeOrderTableRepository implements OrderTableRepository {

    Map<UUID, OrderTable> orderTables = new HashMap<>();


    @Override
    public Optional<OrderTable> findById(UUID orderTableId) {
        return Optional.ofNullable(orderTables.get(orderTableId));
    }

    @Override
    public OrderTable save(OrderTable orderTable) {
        orderTables.put(orderTable.getId(), orderTable);
        return orderTable;
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(orderTables.values());
    }
}
