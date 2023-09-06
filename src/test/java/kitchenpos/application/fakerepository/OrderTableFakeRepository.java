package kitchenpos.application.fakerepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

public class OrderTableFakeRepository implements OrderTableRepository {

    private final Map<UUID, OrderTable> orderTables = new HashMap<>();

    @Override
    public OrderTable save(final OrderTable entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }

        orderTables.put(entity.getId(), entity);

        return entity;
    }

    @Override
    public Optional<OrderTable> findById(final UUID id) {
        return Optional.ofNullable(orderTables.get(id));
    }

    @Override
    public List<OrderTable> findAll() {
        return orderTables.values()
            .stream()
            .collect(Collectors.toUnmodifiableList());
    }
}
