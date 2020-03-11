package kitchenpos.dao;

import kitchenpos.model.OrderTable;
import kitchenpos.support.OrderTableBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryOrderTableDao implements OrderTableDao{

    private Map<Long, OrderTable> entities = new HashMap<>();

    @Override
    public OrderTable save(OrderTable entity) {
        OrderTable savedOrderTable = new OrderTableBuilder()
            .id(entity.getId())
            .tableGroupId(entity.getTableGroupId())
            .numberOfGuests(entity.getNumberOfGuests())
            .empty(entity.isEmpty())
            .build();

        entities.put(entity.getId(), savedOrderTable);

        return savedOrderTable;
    }

    @Override
    public Optional<OrderTable> findById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public List<OrderTable> findAllByIdIn(List<Long> ids) {
        return this.findAll().stream()
            .filter(orderTable -> ids.contains(orderTable.getId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderTable> findAllByTableGroupId(Long tableGroupId) {
        return this.findAll().stream()
            .filter(orderTable -> orderTable.getTableGroupId() == tableGroupId)
            .collect(Collectors.toList());
    }
}
