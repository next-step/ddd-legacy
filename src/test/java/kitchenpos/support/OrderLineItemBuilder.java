package kitchenpos.support;

import kitchenpos.model.OrderLineItem;

public class OrderLineItemBuilder {
    private Long seq;
    private Long orderId;
    private Long menuId;
    private long quantity;

    private OrderLineItemBuilder() {
    }

    public static OrderLineItemBuilder orderLineItem() {
        return new OrderLineItemBuilder();
    }

    public OrderLineItemBuilder withSeq(long seq) {
        this.seq = seq;
        return this;
    }

    public OrderLineItemBuilder withOrderId(long orderId) {
        this.orderId = orderId;
        return this;
    }

    public OrderLineItemBuilder withMenuId(long menuId) {
        this.menuId = menuId;
        return this;
    }

    public OrderLineItemBuilder withQuantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderLineItem build() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(seq);
        orderLineItem.setOrderId(orderId);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

}
