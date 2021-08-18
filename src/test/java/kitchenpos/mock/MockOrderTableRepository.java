package kitchenpos.mock;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;

public class MockOrderTableRepository implements OrderTableRepository {
    private final Map<UUID, OrderTable> orderTableMap = new HashMap<>();

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
