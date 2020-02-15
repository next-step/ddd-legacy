package kitchenpos.dao;

import kitchenpos.model.OrderLineItem;

import java.util.*;
import java.util.stream.Collectors;

public class TestOrerLineItemDao implements OrderLineItemDao {

    private Map<Long, OrderLineItem> orderLineItems = new HashMap<>();

    @Override
    public OrderLineItem save(OrderLineItem entity) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(entity.getSeq());

        orderLineItems.put(entity.getSeq(), entity);
        return entity;
    }

    @Override
    public Optional<OrderLineItem> findById(Long id) {
        Objects.requireNonNull(id);

        return Optional.ofNullable(orderLineItems.get(id));
    }

    @Override
    public List<OrderLineItem> findAll() {
        return new ArrayList<>(orderLineItems.values());
    }

    @Override
    public List<OrderLineItem> findAllByOrderId(Long orderId) {
        Objects.requireNonNull(orderId);

        return orderLineItems.values()
                .stream()
                .filter(i -> i.getOrderId().equals(orderId))
                .collect(Collectors.toList());
    }
}
