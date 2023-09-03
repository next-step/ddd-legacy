package kitchenpos.fake;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;

public class InMemoryOrderTableRepository implements OrderTableRepository {
    private final HashMap<UUID, OrderTable> entities = new HashMap<>();

    @Override
    public OrderTable save(OrderTable entity) {
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public Optional<OrderTable> findById(UUID uuid) {
        return Optional.ofNullable(entities.get(uuid));
    }


}