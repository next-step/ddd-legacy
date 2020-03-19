package kitchenpos.dao;

import kitchenpos.model.OrderLineItem;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryOrderLineItemDao implements OrderLineItemDao {
    private final Map<Long, OrderLineItem> data = new HashMap<>();

    @Override
    public OrderLineItem save(OrderLineItem entity) {
        data.put(entity.getSeq(), entity);
        return entity;
    }

    @Override
    public Optional<OrderLineItem> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<OrderLineItem> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<OrderLineItem> findAllByOrderId(Long orderId) {
        return data.values().stream()
                .filter(orderLineItem -> orderId.equals(orderLineItem.getOrderId()))
                .collect(Collectors.toList());
    }
}
