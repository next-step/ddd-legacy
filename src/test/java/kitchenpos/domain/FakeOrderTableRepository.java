package kitchenpos.domain;

import java.util.*;

public class FakeOrderTableRepository implements OrderTableRepository {
    private Map<UUID, OrderTable> map = new HashMap<>();
    @Override
    public OrderTable save(OrderTable orderTable) {
        map.put(orderTable.getId(), orderTable);
        return orderTable;
    }

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public List<OrderTable> findAll() {
        return null;
    }
}
