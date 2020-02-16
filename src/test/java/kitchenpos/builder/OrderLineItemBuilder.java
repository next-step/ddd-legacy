package kitchenpos.builder;

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
        orderLineItem.setSeq(this.seq);
        orderLineItem.setQuantity(this.quantity);
        orderLineItem.setOrderId(this.orderId);
        orderLineItem.setMenuId(this.menuId);
        return orderLineItem;
    }
}
