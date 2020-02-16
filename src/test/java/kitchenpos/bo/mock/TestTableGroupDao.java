package kitchenpos.bo.mock;

import kitchenpos.dao.Interface.TableGroupDao;
import kitchenpos.model.TableGroup;

import java.util.HashMap;
import java.util.Map;

public class TestTableGroupDao implements TableGroupDao {

    private static final Map<Long, TableGroup> data = new HashMap<>();

    @Override
    public TableGroup save(TableGroup entity) {
        data.put(entity.getId(), entity);
        return entity;
    }
}
