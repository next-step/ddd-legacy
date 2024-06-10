package kitchenpos.fake.ordertable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

public class TestOrderTableRepository implements OrderTableRepository {
    private final ConcurrentHashMap<UUID, OrderTable> orderTables = new ConcurrentHashMap<>();

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return Optional.ofNullable(orderTables.get(id));
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
