package kitchenpos.application.fake.helper;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;

public class InMemoryOrderTableRepository implements OrderTableRepository {

    private final Map<UUID, OrderTable> elements = new HashMap<>();

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return Optional.ofNullable(elements.get(id));
    }

    @Override
    public OrderTable save(OrderTable table) {
        elements.put(table.getId(), table);
        return table;
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(elements.values());
    }
}
