package kitchenpos.fake;

import kitchenpos.dao.TableGroupDao;
import kitchenpos.model.TableGroup;

import java.util.*;

public class FakeTableGroupDao implements TableGroupDao {
    private Map<Long, TableGroup> entities = new HashMap<>();

    @Override
    public TableGroup save(TableGroup entity) {
        entities.put(entity.getId(), entity);
        return entity;
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
