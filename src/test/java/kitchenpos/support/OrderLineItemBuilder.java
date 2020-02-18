package kitchenpos.support;

import kitchenpos.model.OrderLineItem;

public class OrderLineItemBuilder {
    private Long seq;
    private Long orderId;
    private Long menuId;
    private long quantity;

    public OrderLineItemBuilder seq (Long seq){
        this.seq = seq;
        return this;
    }

    public OrderLineItemBuilder orderId(Long orderId){
        this.orderId = orderId;
        return this;
    }

    public OrderLineItemBuilder menuId(Long menuId){
        this.menuId = menuId;
        return this;
    }

    public OrderLineItemBuilder quantity(Long quantity){
        this.quantity = quantity;
        return this;
    }

    public OrderLineItem build(){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(this.seq);
        orderLineItem.setOrderId(this.orderId);
        orderLineItem.setMenuId(this.menuId);
        orderLineItem.setQuantity(this.quantity);

        return orderLineItem;
    }
}
