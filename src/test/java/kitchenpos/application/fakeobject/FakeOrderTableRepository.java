package kitchenpos.application.fakeobject;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;

public class FakeOrderTableRepository implements OrderTableRepository {
    private Map<UUID, OrderTable> orderTableMap = new HashMap<>();

    public FakeOrderTableRepository() {
        for (int i = 1; i <= 5; i++) {
            OrderTable orderTable = new OrderTable();
            UUID id = UUID.fromString("3faec3ab-5217-405d-aaa2-804f87697f8" + i);
            orderTable.setId(id);
            orderTableMap.put(id, orderTable);
        }
    }

    @Override
    public Optional<OrderTable> findById(UUID orderTableId) {
        if (orderTableMap.containsKey(orderTableId)) {
            return Optional.of(orderTableMap.get(orderTableId));
        }
        return Optional.empty();
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(orderTableMap.values());
    }

    @Override
    public OrderTable save(OrderTable orderTable) {
        if (orderTable.getId() != null && orderTableMap.containsKey(orderTable.getId())) {
            orderTableMap.put(orderTable.getId(), orderTable);
            return orderTable;
        }
        orderTable.setId(UUID.randomUUID());
        orderTableMap.put(orderTable.getId(), orderTable);
        return orderTable;
    }
}
