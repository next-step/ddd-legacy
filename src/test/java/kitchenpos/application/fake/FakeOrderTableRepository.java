package kitchenpos.application.fake;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;

public class FakeOrderTableRepository implements OrderTableRepository {

    private final Map<UUID, OrderTable> orderTableMap = new HashMap<>();

    @Override
    public OrderTable save(OrderTable orderTable) {
        UUID uuid = UUID.randomUUID();
        orderTable.setId(uuid);
        orderTableMap.put(uuid, orderTable);
        return orderTable;
    }

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return Optional.ofNullable(orderTableMap.get(id));
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(orderTableMap.values());
    }

}
