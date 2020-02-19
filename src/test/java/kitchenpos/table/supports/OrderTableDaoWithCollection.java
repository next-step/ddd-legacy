package kitchenpos.table.supports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderTable;

public class OrderTableDaoWithCollection implements OrderTableDao {

    private long id;
    private final Map<Long, OrderTable> entities;

    public OrderTableDaoWithCollection() {
        this.id = 0;
        this.entities = new HashMap<>();
    }

    public OrderTableDaoWithCollection(List<OrderTable> entities) {
        this.entities = entities.stream()
                                .peek(e -> e.setId(++id))
                                .collect(Collectors.toMap(OrderTable::getId,
                                                          Function.identity()));
    }

    @Override
    public OrderTable save(OrderTable entity) {
        if (entity.getId() == null) { entity.setId(++id); }
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<OrderTable> findById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<OrderTable> findAll() {
        return null;
    }

    @Override
    public List<OrderTable> findAllByIdIn(List<Long> ids) {
        return entities.values()
                       .stream()
                       .filter(ot -> ids.contains(ot.getId()))
                       .collect(Collectors.toList());
    }

    @Override
    public List<OrderTable> findAllByTableGroupId(Long tableGroupId) {
        return entities.values()
                       .stream()
                       .filter(ot -> tableGroupId.equals(ot.getTableGroupId()))
                       .collect(Collectors.toList());
    }
}
