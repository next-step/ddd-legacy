package kitchenpos.fake;

import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderTable;

import java.util.*;
import java.util.stream.Collectors;

public class FakeOrderTableDao implements OrderTableDao {
    private Map<Long, OrderTable> entities = new HashMap<>();

    @Override
    public OrderTable save(OrderTable entity) {
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<OrderTable> findById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public List<OrderTable> findAllByIdIn(List<Long> ids) {
        return entities.values()
                .stream()
                .filter(orderTable -> ids.contains(orderTable.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderTable> findAllByTableGroupId(Long tableGroupId) {
        return entities.values()
                .stream()
                .filter(orderTable -> orderTable.getTableGroupId().equals(tableGroupId))
                .collect(Collectors.toList());
    }
}
