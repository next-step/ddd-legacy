package kitchenpos.dao;

import kitchenpos.model.OrderLineItem;

import java.util.List;
import java.util.Optional;

public interface DefaultOrderLineItemDao {
    OrderLineItem save(OrderLineItem entity);

    Optional<OrderLineItem> findById(Long id);

    List<OrderLineItem> findAll();

    List<OrderLineItem> findAllByOrderId(Long orderId);
}
