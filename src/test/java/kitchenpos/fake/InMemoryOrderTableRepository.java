package kitchenpos.fake;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;

public class InMemoryOrderTableRepository implements OrderTableRepository {
    private final Map<UUID, OrderTable> store = new HashMap<>();

    @Override
    public OrderTable save(OrderTable orderTable) {
        if (orderTable.getId() == null) {
            orderTable.setId(UUID.randomUUID());
        }
        store.put(orderTable.getId(), orderTable);
        return orderTable;
    }

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(store.values());
    }
}
