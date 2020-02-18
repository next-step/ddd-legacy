package kitchenpos.dao;

import kitchenpos.model.OrderTable;

import java.util.List;
import java.util.Optional;

public interface DefaultOrderTableDao {
    OrderTable save(OrderTable entity);

    Optional<OrderTable> findById(Long id);

    List<OrderTable> findAll();

    List<OrderTable> findAllByIdIn(List<Long> ids);

    List<OrderTable> findAllByTableGroupId(Long tableGroupId);

    OrderTable select(Long id);
}
