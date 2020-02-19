package kitchenpos.table.group.supports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.TableGroup;

public class TableGroupDaoWithCollection implements TableGroupDao {

    private long id;
    private final Map<Long, TableGroup> entities;

    public TableGroupDaoWithCollection() {
        this.id = 0;
        this.entities = new HashMap<>();
    }

    public TableGroupDaoWithCollection(List<TableGroup> entities) {
        this.entities = entities.stream()
                                .peek(e -> e.setId(++id))
                                .collect(Collectors.toMap(TableGroup::getId,
                                                          Function.identity()));
    }

    @Override
    public TableGroup save(TableGroup entity) {
        if (entity.getId() == null) { entity.setId(++id); }
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<TableGroup> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<TableGroup> findAll() {
        return null;
    }
}
