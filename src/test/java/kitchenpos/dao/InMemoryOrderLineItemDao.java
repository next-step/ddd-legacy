package kitchenpos.dao;

import kitchenpos.model.OrderLineItem;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryOrderLineItemDao implements DefaultOrderLineItemDao {
    private Map<Long, OrderLineItem> data = new HashMap<>();

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
                .filter(orderLineItem -> orderLineItem.getOrderId().equals(orderId))
                .collect(Collectors.toList());
    }
}
