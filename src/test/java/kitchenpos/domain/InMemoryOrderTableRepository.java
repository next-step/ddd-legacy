package kitchenpos.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryOrderTableRepository implements OrderTableRepository {

    private final Map<UUID, OrderTable> elements = new HashMap<>();

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return Optional.ofNullable(elements.get(id));
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(elements.values());
    }

    @Override
    public OrderTable save(OrderTable orderTable) {
        elements.put(orderTable.getId(), orderTable);
        return orderTable;
    }
}
