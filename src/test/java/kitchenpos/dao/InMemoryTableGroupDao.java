package kitchenpos.dao;

import kitchenpos.model.TableGroup;

import java.util.*;

public class InMemoryTableGroupDao implements TableGroupDao {
    private final Map<Long, TableGroup> data = new HashMap<>();

    @Override
    public TableGroup save(TableGroup entity) {
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<TableGroup> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<TableGroup> findAll() {
        return new ArrayList<>(data.values());
    }
}
