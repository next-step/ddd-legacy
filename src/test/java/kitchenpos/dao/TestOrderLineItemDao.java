package kitchenpos.dao;

import kitchenpos.model.OrderLineItem;

import java.util.*;
import java.util.stream.Collectors;

public class TestOrderLineItemDao implements OrderLineItemDao {

    private final Map<Long, OrderLineItem> orderLineItems = new HashMap();

    @Override
    public OrderLineItem save(OrderLineItem entity) {
        orderLineItems.put(entity.getSeq(), entity);
        return entity;
    }

    @Override
    public Optional<OrderLineItem> findById(Long id) {
        return Optional.ofNullable(orderLineItems.get(id));
    }

    @Override
    public List<OrderLineItem> findAll() {
        return new ArrayList(orderLineItems.values());
    }

    @Override
    public List<OrderLineItem> findAllByOrderId(Long orderId) {
        if (Objects.isNull(orderId)) {
            return Collections.EMPTY_LIST;
        }

        return orderLineItems.values()
                .stream()
                .filter(orderLineItem -> orderId.equals(orderLineItem.getOrderId()))
                .collect(Collectors.toList());
    }
}
