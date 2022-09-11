package kitchenpos.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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
        orderTables.put(orderTable.getId(), orderTable);
        return orderTable;
    }

    @Override
    public List<OrderTable> findAll() {
        return orderTables.values()
            .stream()
            .collect(Collectors.toUnmodifiableList());
    }
}
