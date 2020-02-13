package kitchenpos.builder;

import kitchenpos.model.OrderLineItem;

public class OrderLineItemBuilder {
    private Long seq;
    private Long orderId;
    private Long menuId;
    private long quantity;

    public OrderLineItemBuilder setSeq(Long seq) {
        this.seq = seq;
        return this;
    }

    public OrderLineItemBuilder setOrderId(Long orderId) {
        this.orderId = orderId;
        return this;
    }

    public OrderLineItemBuilder setMenuId(Long menuId) {
        this.menuId = menuId;
        return this;
    }

    public OrderLineItemBuilder setQuantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderLineItem build() {
        OrderLineItem orderLineItem = new OrderLineItem();

        orderLineItem.setMenuId(menuId);
        orderLineItem.setOrderId(orderId);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setSeq(seq);

        return orderLineItem;
    }
}
