package kitchenpos.dao;

import kitchenpos.model.TableGroup;

import java.util.*;

public class TestTableGroupDao implements TableGroupDao {

    private Map<Long, TableGroup> tableGroups = new HashMap<>();

    @Override
    public TableGroup save(TableGroup entity) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(entity.getId());

        tableGroups.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<TableGroup> findById(Long id) {
        return Optional.ofNullable(tableGroups.get(id));
    }

    @Override
    public List<TableGroup> findAll() {
        return new ArrayList<>(tableGroups.values());
    }
}
