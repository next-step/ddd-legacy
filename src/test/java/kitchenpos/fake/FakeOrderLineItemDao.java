package kitchenpos.fake;

import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.model.OrderLineItem;

import java.util.*;
import java.util.stream.Collectors;

public class FakeOrderLineItemDao implements OrderLineItemDao {
    private Map<Long, OrderLineItem> entities = new HashMap<>();

    @Override
    public OrderLineItem save(OrderLineItem entity) {
        entities.put(entity.getSeq(), entity);
        return entity;
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
        return entities.values()
                .stream()
                .filter(orderLineItem -> orderLineItem.getOrderId().equals(orderId))
                .collect(Collectors.toList());
    }
}
