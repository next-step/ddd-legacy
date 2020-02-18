package kitchenpos.dao;

import kitchenpos.model.OrderTable;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryOrderTableDao implements DefaultOrderTableDao {
    private Map<Long, OrderTable> data = new HashMap<>();

    @Override
    public OrderTable save(OrderTable entity) {
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<OrderTable> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<OrderTable> findAllByIdIn(List<Long> ids) {
        return data.values().stream()
                .filter(orderTable -> ids.contains(orderTable.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderTable> findAllByTableGroupId(Long tableGroupId) {
        return data.values().stream()
                .filter(orderTable -> orderTable.getTableGroupId().equals(tableGroupId))
                .collect(Collectors.toList());
    }

    @Override
    public OrderTable select(Long id) {
        return data.get(id);
    }
}
