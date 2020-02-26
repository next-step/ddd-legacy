package kitchenpos.bo.mock;

import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderTable;

import java.util.*;
import java.util.stream.Collectors;

public class TestOrderTableDao implements OrderTableDao {

    private static final Map<Long, OrderTable> data = new HashMap<>();

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
        return data.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderTable> findAllByIdIn(List<Long> ids) {
        return ids.stream()
                .filter(id -> Objects.nonNull(data.get(id)))
                .map(id -> data.get(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderTable> findAllByTableGroupId(Long tableGroupId) {
        return data.values()
                .stream()
                .filter(orderTable -> orderTable.getTableGroupId() == tableGroupId)
                .collect(Collectors.toList());
    }
}
