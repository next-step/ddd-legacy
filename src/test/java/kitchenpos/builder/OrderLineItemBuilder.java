package kitchenpos.builder;

import kitchenpos.model.OrderLineItem;

public class OrderLineItemBuilder {
    private Long seq;
    private Long orderId;
    private Long menuId;
    private long quantity;

    public OrderLineItemBuilder() {
    }

    public OrderLineItemBuilder seq(Long val) {
        seq = val;
        return this;
    }

    public OrderLineItemBuilder orderId(Long val) {
        orderId = val;
        return this;
    }

    public OrderLineItemBuilder menuId(Long val) {
        menuId = val;
        return this;
    }

    public OrderLineItemBuilder quantity(long val) {
        quantity = val;
        return this;
    }

    public OrderLineItem build() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(this.seq);
        orderLineItem.setOrderId(this.orderId);
        orderLineItem.setMenuId(this.menuId);
        orderLineItem.setQuantity(this.quantity);
        return orderLineItem;
    }

}
