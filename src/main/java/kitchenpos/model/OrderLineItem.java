package kitchenpos.model;

public class OrderLineItem {
    private Long seq;
    private Long orderId;
    private Long menuId;
    private long quantity;

    private OrderLineItem (Builder builder){
        this.seq = builder.seq;
        this.orderId = builder.orderId;
        this.menuId = builder.menuId;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(final Long seq) {
        this.seq = seq;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(final Long menuId) {
        this.menuId = menuId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(final long quantity) {
        this.quantity = quantity;
    }

    public static class Builder {
        private Long seq;
        private Long orderId;
        private Long menuId;
        private long quantity;

        public Builder seq (Long seq){
            this.seq = seq;
            return this;
        }

        public Builder orderId (Long orderId){
            this.orderId = orderId;
            return this;
        }

        public Builder menuId (Long menuId){
            this.menuId = menuId;
            return this;
        }

        public Builder quantity(long quantity){
            this.quantity = quantity;
            return this;
        }

        public OrderLineItem build(){
            return new OrderLineItem(this);
        }
    }
}
