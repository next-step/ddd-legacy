package kitchenpos.model;

public final class OrderLineItemBuilder {
    private Long seq;
    private Long orderId;
    private Long menuId;
    private long quantity;

    private OrderLineItemBuilder() {}

    public static OrderLineItemBuilder anOrderLineItem() { return new OrderLineItemBuilder(); }

    public OrderLineItemBuilder withSeq(Long seq) {
        this.seq = seq;
        return this;
    }

    public OrderLineItemBuilder withOrderId(Long orderId) {
        this.orderId = orderId;
        return this;
    }

    public OrderLineItemBuilder withMenuId(Long menuId) {
        this.menuId = menuId;
        return this;
    }

    public OrderLineItemBuilder withQuantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderLineItem build() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(seq);
        orderLineItem.setOrderId(orderId);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }
}
