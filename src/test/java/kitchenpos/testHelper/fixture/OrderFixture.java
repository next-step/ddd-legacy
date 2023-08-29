package kitchenpos.testHelper.fixture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;

public class OrderFixture {

    public static class EatInOrderCreateRequestBuilder extends TakeOutOrderCreateRequestBuilder {

        private UUID orderTableId;


        public EatInOrderCreateRequestBuilder orderTable(final UUID orderTableId) {
            this.orderTableId = orderTableId;

            return this;
        }

        @Override
        public EatInOrderCreateRequestBuilder menu(Menu menu, long quantity, BigDecimal price) {
            super.menu(menu, quantity, price);

            return this;
        }

        @Override
        public EatInOrderCreateRequestBuilder type(OrderType type) {
            super.type(type);

            return this;
        }

        @Override
        public Order build() {
            Order order = super.build();
            order.setOrderTableId(this.orderTableId);

            return order;
        }
    }

    public static EatInOrderCreateRequestBuilder createEatInOrderRequestBuilder() {
        return new EatInOrderCreateRequestBuilder();
    }

    public static class DeliveryCreateRequestBuilder extends TakeOutOrderCreateRequestBuilder {

        private String deliveryAddress;

        public DeliveryCreateRequestBuilder deliveryAddress(final String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;

            return this;
        }

        @Override
        public DeliveryCreateRequestBuilder menu(Menu menu, long quantity, BigDecimal price) {
            super.menu(menu, quantity, price);
            return this;
        }

        @Override
        public DeliveryCreateRequestBuilder type(OrderType type) {
            super.type(type);
            return this;
        }

        @Override
        public Order build() {
            Order order = super.build();
            order.setDeliveryAddress(this.deliveryAddress);

            return order;
        }
    }

    public static DeliveryCreateRequestBuilder createDeliveryOrderRequestBuilder() {
        return new DeliveryCreateRequestBuilder();
    }

    public static class TakeOutOrderCreateRequestBuilder {

        private List<OrderLineItem> orderLineItems = new ArrayList<>();
        private OrderType type;

        public TakeOutOrderCreateRequestBuilder menu(final Menu menu, final long quantity, final BigDecimal price) {
            OrderLineItem orderLineItem = new OrderLineItem();
            orderLineItem.setMenuId(menu.getId());
            orderLineItem.setQuantity(quantity);
            orderLineItem.setPrice(price);

            orderLineItems.add(orderLineItem);

            return this;
        }

        public TakeOutOrderCreateRequestBuilder type(final OrderType type) {
            this.type = type;

            return this;
        }

        public Order build() {
            Order order = new Order();
            order.setOrderLineItems(this.orderLineItems);
            order.setType(this.type);

            return order;
        }
    }

    public static TakeOutOrderCreateRequestBuilder createTakeOutOrderCreateRequestBuilder() {
        return new TakeOutOrderCreateRequestBuilder();
    }

}
