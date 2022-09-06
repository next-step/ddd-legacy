package kitchenpos.integration.mock;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MemoryOrderTableRepository implements OrderTableRepository {
    private static final Map<UUID, OrderTable> STORE = new HashMap<>();

    @Override
    public Optional<OrderTable> findById(UUID id) {
        if (!STORE.containsKey(id)) {
            return Optional.empty();
        }

        return Optional.of(STORE.get(id));
    }

    @Override
    public OrderTable save(OrderTable table) {
        STORE.put(table.getId(), table);
        return table;
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(STORE.values());
    }

    public void clear() {
        STORE.clear();
    }
}
