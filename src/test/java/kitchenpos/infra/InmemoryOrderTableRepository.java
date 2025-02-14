package kitchenpos.infra;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InmemoryOrderTableRepository implements OrderTableRepository {

    IdGenerator idGenerator = UUID::randomUUID;
    private final Map<UUID, OrderTable> store = new ConcurrentHashMap<>();

    @Override
    public OrderTable save(OrderTable orderTable) {
        if (orderTable.getId() == null) {
            orderTable.setId(idGenerator.random());
        }
        store.put(orderTable.getId(), orderTable);
        return orderTable;
    }

    @Override
    public Optional<OrderTable> findById(UUID orderTableId) {
        return Optional.ofNullable(store.get(orderTableId));
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(store.values());
    }
}
