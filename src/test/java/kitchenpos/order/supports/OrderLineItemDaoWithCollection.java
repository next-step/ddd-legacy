package kitchenpos.order.supports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.model.OrderLineItem;

public class OrderLineItemDaoWithCollection implements OrderLineItemDao {

    private long sequence = 0;
    private final Map<Long, OrderLineItem> entities;

    public OrderLineItemDaoWithCollection() {
        this.entities = new HashMap<>();
    }

    public OrderLineItemDaoWithCollection(List<OrderLineItem> entities) {
        this.entities = entities.stream()
                                .peek(e -> e.setSeq(++sequence))
                                .collect(Collectors.toMap(OrderLineItem::getSeq,
                                                          Function.identity()));
    }

    @Override
    public OrderLineItem save(OrderLineItem entity) {
        if (entity.getSeq() == null) { entity.setSeq(++sequence); }
        entities.put(entity.getSeq(), entity);
        return entity;
    }

    @Override
    public Optional<OrderLineItem> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<OrderLineItem> findAll() {
        return null;
    }

    @Override
    public List<OrderLineItem> findAllByOrderId(Long orderId) {
        return null;
    }
}
