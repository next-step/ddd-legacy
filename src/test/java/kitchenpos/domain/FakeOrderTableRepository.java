package kitchenpos.domain;

import java.util.*;

public class FakeOrderTableRepository implements OrderTableRepository {
    Map<UUID, OrderTable> orderTableMap = new LinkedHashMap<>();

    @Override
    public OrderTable save(final OrderTable orderTable) {
        orderTableMap.put(orderTable.getId(), orderTable);
        return orderTable;
    }

    @Override
    public Optional<OrderTable> findById(final UUID orderTableId) {
        return Optional.ofNullable(orderTableMap.get(orderTableId));
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(orderTableMap.values());
    }
}
