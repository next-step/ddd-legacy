package kitchenpos.dao;

import kitchenpos.model.TableGroup;
import kitchenpos.support.TableGroupBuilder;

import java.util.*;

public class InMemoryTableGroupDao implements TableGroupDao {

    private Map<Long, TableGroup> entities = new HashMap<>();

    @Override
    public TableGroup save(TableGroup entity) {
        TableGroup tableGroup = new TableGroupBuilder()
            .id(entity.getId())
            .createdDate(entity.getCreatedDate())
            .orderTables(new ArrayList<>(entity.getOrderTables()))
            .build();

        entities.put(tableGroup.getId(), tableGroup);
        return tableGroup;
    }

    @Override
    public Optional<TableGroup> findById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<TableGroup> findAll() {
        return new ArrayList<>(entities.values());
    }
}
