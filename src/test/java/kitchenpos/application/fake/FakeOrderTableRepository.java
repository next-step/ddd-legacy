package kitchenpos.application.fake;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.assertj.core.util.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeOrderTableRepository implements OrderTableRepository {
    private final Map<UUID, OrderTable> memoryMap = new HashMap<>();

    @Override
    public OrderTable save(OrderTable orderTable) {
        memoryMap.put(orderTable.getId(), orderTable);
        return orderTable;
    }

    @Override
    public Optional<OrderTable> findById(UUID orderTableId) {
        return Optional.ofNullable(memoryMap.get(orderTableId));
    }

    @Override
    public List<OrderTable> findAll() {
        return Lists.newArrayList(memoryMap.values());
    }
}
