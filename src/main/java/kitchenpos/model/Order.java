package kitchenpos.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Long id;
    private Long orderTableId;
    private String orderStatus;
    private LocalDateTime orderedTime;
    private List<OrderLineItem> orderLineItems;

    private Order (Builder builder){
        this.id = builder.id;
        this.orderTableId = builder.orderTableId;
        this.orderStatus = builder.orderStatus;
        this.orderedTime = builder.orderedTime;
        this.orderLineItems = builder.orderLineItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getOrderTableId() {
        return orderTableId;
    }

    public void setOrderTableId(final Long orderTableId) {
        this.orderTableId = orderTableId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(final String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderedTime() {
        return orderedTime;
    }

    public void setOrderedTime(final LocalDateTime orderedTime) {
        this.orderedTime = orderedTime;
    }

    public List<OrderLineItem> getOrderLineItems() {
        return orderLineItems;
    }

    public void setOrderLineItems(final List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
    }

    public void addOrderLineItem (OrderLineItem orderLineItem){
        this.orderLineItems.add(orderLineItem);
    }

    public static class Builder {
        private Long id;
        private Long orderTableId;
        private String orderStatus;
        private LocalDateTime orderedTime;
        private List<OrderLineItem> orderLineItems;

        public Builder id (Long id){
            this.id = id;
            return this;
        }

        public Builder orderTableId (Long orderTableId){
            this.orderTableId = orderTableId;
            return this;
        }

        public Builder orderStatus (String orderStatus){
            this.orderStatus = orderStatus;
            return this;
        }

        public Builder orderedTime (LocalDateTime orderedTime){
            this.orderedTime = orderedTime;
            return this;
        }

        public Builder orderLineItems (List<OrderLineItem> orderLineItems){
            this.orderLineItems = orderLineItems;
            return this;
        }

        public Order build(){
            return new Order(this);
        }
    }
}
