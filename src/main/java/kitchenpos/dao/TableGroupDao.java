package kitchenpos.dao;

import java.util.List;
import java.util.Optional;

import kitchenpos.model.TableGroup;

public interface TableGroupDao {
    TableGroup save(TableGroup entity);

    Optional<TableGroup> findById(Long id);

    List<TableGroup> findAll();
}
