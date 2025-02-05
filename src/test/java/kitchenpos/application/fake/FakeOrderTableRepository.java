package kitchenpos.application.fake;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;

public class FakeOrderTableRepository implements OrderTableRepository {
    Map<UUID,OrderTable> memory = new HashMap<>();
    @Override
    public Optional<OrderTable> findById(UUID orderTableId) {
        return Optional.ofNullable(memory.get(orderTableId));
    }

    @Override
    public OrderTable save(OrderTable orderTable) {
        memory.put(orderTable.getId(), orderTable);
        return memory.get(orderTable.getId());
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(memory.values());
    }
}
