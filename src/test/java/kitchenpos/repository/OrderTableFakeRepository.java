package kitchenpos.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

public class OrderTableFakeRepository implements OrderTableRepository {

    private final List<OrderTable> orderTables = new ArrayList<>();

    @Override
    public List<OrderTable> findAll() {
        return Collections.unmodifiableList(orderTables);
    }

    @Override
    public OrderTable save(OrderTable entity) {
        orderTables.add(entity);
        return entity;
    }

    @Override
    public Optional<OrderTable> findById(UUID uuid) {
        return orderTables.stream()
            .filter(it -> uuid.equals(it.getId()))
            .findAny();
    }
}
