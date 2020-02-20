package kitchenpos.dao;

import kitchenpos.model.OrderTable;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TestOrderTableDao implements OrderTableDao {

    private final Map<Long, OrderTable> orderTables = new HashMap();

    @Override
    public OrderTable save(OrderTable entity) {
        orderTables.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<OrderTable> findById(Long id) {
        return Optional.ofNullable(orderTables.get(id));
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList(orderTables.values());
    }

    @Override
    public List<OrderTable> findAllByIdIn(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.EMPTY_LIST;
        }

        return orderTables.values()
                .stream()
                .filter(orderTable -> ids.contains(orderTable.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderTable> findAllByTableGroupId(Long tableGroupId) {
        if (Objects.isNull(tableGroupId)) {
            return Collections.EMPTY_LIST;
        }

        return orderTables.values()
                .stream()
                .filter(value -> tableGroupId.equals(value.getTableGroupId()))
                .collect(Collectors.toList());
    }
}
