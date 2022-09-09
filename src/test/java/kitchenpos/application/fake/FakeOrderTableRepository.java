package kitchenpos.application.fake;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeOrderTableRepository implements OrderTableRepository {

    private Map<UUID, OrderTable> fakePersistence = new HashMap<>();

    @Override
    public OrderTable save(OrderTable orderTable) {
        if (fakePersistence.containsKey(orderTable.getId())) {
            throw new IllegalArgumentException("duplicate primary key");
        }
        return fakePersistence.put(orderTable.getId(), orderTable);
    }

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return Optional.ofNullable(fakePersistence.get(id));
    }

    @Override
    public List<OrderTable> findAll() {
        return fakePersistence.values()
                .stream()
                .collect(Collectors.toList());
    }
}
