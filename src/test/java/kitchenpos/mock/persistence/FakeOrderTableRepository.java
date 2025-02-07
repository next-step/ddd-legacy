package kitchenpos.mock.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

public class FakeOrderTableRepository implements OrderTableRepository {

    private final Map<UUID, OrderTable> dataMap = new ConcurrentHashMap<>();

    @Override
    public OrderTable save(OrderTable orderTable) {
        if (orderTable.getId() == null) {
            orderTable.setId(UUID.randomUUID());
        }
        UUID id = orderTable.getId();
        dataMap.put(id, orderTable);
        return orderTable;
    }

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return dataMap.values()
            .stream()
            .filter(orderTable -> orderTable.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(dataMap.values());
    }
}
