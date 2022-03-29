package kitchenpos.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

public class InMemoryOrderTableRepository implements OrderTableRepository {

    private final Map<UUID, OrderTable> orderTables = new HashMap<>();

    @Override
    public Optional<OrderTable> findById(UUID orderTableId) {
        return Optional.ofNullable(orderTables.get(orderTableId));
    }

    @Override
    public OrderTable save(OrderTable orderTable) {
        if (Objects.isNull(orderTable.getId())) {
            orderTable.setId(UUID.randomUUID());
        }
        orderTables.put(orderTable.getId(), orderTable);
        return orderTable;
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(orderTables.values());
    }
}
