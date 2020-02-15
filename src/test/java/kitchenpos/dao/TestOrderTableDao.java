package kitchenpos.dao;

import kitchenpos.model.OrderTable;

import java.util.*;
import java.util.stream.Collectors;

public class TestOrderTableDao implements OrderTableDao {

    Map<Long, OrderTable> orderTables = new HashMap<>();

    @Override
    public OrderTable save(OrderTable entity) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(entity.getId());

        orderTables.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<OrderTable> findById(Long id) {
        Objects.requireNonNull(id);

        return Optional.ofNullable(orderTables.get(id));
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(orderTables.values());
    }

    @Override
    public List<OrderTable> findAllByIdIn(List<Long> ids) {
        Objects.requireNonNull(ids);

        return orderTables.values()
                .stream()
                .filter(i -> ids.contains(i.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderTable> findAllByTableGroupId(Long tableGroupId) {
        Objects.requireNonNull(tableGroupId);

        return orderTables.values()
                .stream()
                .filter(i -> tableGroupId.equals(i.getTableGroupId()))
                .collect(Collectors.toList());
    }
}
