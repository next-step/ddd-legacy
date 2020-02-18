package kitchenpos.dao;

import java.util.List;
import java.util.Optional;

import kitchenpos.model.OrderLineItem;

public interface OrderLineItemDao {
    OrderLineItem save(OrderLineItem entity);

    Optional<OrderLineItem> findById(Long id);

    List<OrderLineItem> findAll();

    List<OrderLineItem> findAllByOrderId(Long orderId);
}
