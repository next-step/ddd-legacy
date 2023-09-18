package kitchenpos.fake;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeOrderTableRepository implements OrderTableRepository {

    private Map<UUID, OrderTable> orderTables = new HashMap<>();

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return Optional.ofNullable(orderTables.get(id));
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
                .collect(Collectors.toList());
    }

}
