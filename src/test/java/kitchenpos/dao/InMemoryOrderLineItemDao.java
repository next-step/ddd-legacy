package kitchenpos.dao;

import kitchenpos.model.OrderLineItem;
import kitchenpos.support.OrderLineItemBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryOrderLineItemDao implements OrderLineItemDao {

    private Map<Long, OrderLineItem> entities = new HashMap<>();

    @Override
    public OrderLineItem save(OrderLineItem entity) {
        OrderLineItem orderLineItem = new OrderLineItemBuilder()
            .seq(entity.getSeq())
            .orderId(entity.getOrderId())
            .menuId(entity.getMenuId())
            .quantity(entity.getQuantity())
            .build();
        return orderLineItem;
    }

    @Override
    public Optional<OrderLineItem> findById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<OrderLineItem> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public List<OrderLineItem> findAllByOrderId(Long orderId) {
        return findAll().stream()
            .filter(orderLineItem -> orderLineItem.getOrderId() == orderId)
            .collect(Collectors.toList());
    }
}
