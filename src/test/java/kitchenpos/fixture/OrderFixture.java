package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static class OrderBuilder {
        private UUID id;
        private OrderType type;
        private OrderStatus status;
        private LocalDateTime orderDateTime;
        private List<OrderLineItem> orderLineItems;
        private String deliveryAddress;
        private OrderTable orderTable;
        private UUID orderTableId;

        public OrderBuilder() {
            this.id = UUID.randomUUID();
        }

        public OrderBuilder type(OrderType orderType) {
            this.type=orderType;
            return this;
        }

        public OrderBuilder status(OrderStatus orderStatus) {
            this.status=orderStatus;
            return this;
        }

        public OrderBuilder orderDateTime(LocalDateTime orderDateTime) {
            this.orderDateTime = orderDateTime;
            return this;
        }

        public OrderBuilder orderLineItems(List<OrderLineItem> orderLineItems) {
            this.orderLineItems=orderLineItems;
            return this;
        }

        public OrderBuilder deliveryAddress(List<OrderLineItem> orderLineItems) {
            this.orderLineItems=orderLineItems;
            return this;
        }

        public OrderBuilder orderTable(OrderTable orderTable) {
            this.orderTable = orderTable;
            return this;
        }

        public OrderBuilder orderTableId(UUID orderTableId) {
            this.orderTableId = orderTableId;
            return this;
        }

        public Order build() {
            Order order = new Order();
            order.setId(this.id);
            order.setType(this.type);
            order.setStatus(this.status);
            order.setOrderDateTime(this.orderDateTime);
            order.setOrderLineItems(this.orderLineItems);
            order.setDeliveryAddress(this.deliveryAddress);
            order.setOrderTable(this.orderTable);
            order.setOrderTableId(this.orderTableId);
            return order;
        }
    }
}
