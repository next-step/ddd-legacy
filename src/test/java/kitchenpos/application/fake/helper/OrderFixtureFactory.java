package kitchenpos.application.fake.helper;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class OrderFixtureFactory {

    public static final Order 미트파이_레몬에이드_세트_오션뷰_테이블_식사_요청 = new Builder()
            .type(OrderType.EAT_IN)
            .addOrderLineItem(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴, 1)
            .orderTable(OrderTableFixtureFactory.오션뷰_테이블_02_이용중)
            .build();

    public static final Order 테이블에서_식사중인_주문 = new Builder()
            .id(UUID.randomUUID())
            .type(OrderType.EAT_IN)
            .status(OrderStatus.SERVED)
            .addOrderLineItem(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴, 1)
            .orderDateTime(LocalDateTime.now())
            .orderTable(OrderTableFixtureFactory.오션뷰_테이블_02_이용중)
            .build();


    public static final Order 테이블에서_식사가_완료된_주문 = new Builder()
            .id(UUID.randomUUID())
            .type(OrderType.EAT_IN)
            .status(OrderStatus.COMPLETED)
            .addOrderLineItem(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴, 1)
            .orderDateTime(LocalDateTime.now())
            .orderTable(OrderTableFixtureFactory.오션뷰_테이블_02_이용중)
            .build();

    public static final Order 테이블에서_식사가_서빙된_주문 = new Builder()
            .id(UUID.randomUUID())
            .type(OrderType.EAT_IN)
            .status(OrderStatus.SERVED)
            .addOrderLineItem(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴, 1)
            .orderDateTime(LocalDateTime.now())
            .orderTable(OrderTableFixtureFactory.오션뷰_테이블_02_이용중)
            .build();

    public static final Order 대기중인_포장_주문 = new Builder()
            .id(UUID.randomUUID())
            .type(OrderType.TAKEOUT)
            .status(OrderStatus.WAITING)
            .addOrderLineItem(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴, 1)
            .orderDateTime(LocalDateTime.now())
            .build();

    public static final Order 승인된_포장_주문 = new Builder()
            .id(UUID.randomUUID())
            .type(OrderType.TAKEOUT)
            .status(OrderStatus.ACCEPTED)
            .addOrderLineItem(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴, 1)
            .orderDateTime(LocalDateTime.now())
            .build();


    public static final Order 배달이_완료된_주문 = new Builder()
            .id(UUID.randomUUID())
            .type(OrderType.DELIVERY)
            .status(OrderStatus.DELIVERED)
            .addOrderLineItem(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴, 1)
            .orderDateTime(LocalDateTime.now())
            .build();

    public static final class Builder implements FixtureBuilder<Order> {

        private UUID id;
        private OrderType type;
        private OrderStatus status;
        private LocalDateTime orderDateTime;
        private List<OrderLineItem> orderLineItems = new ArrayList<>();
        private String deliveryAddress;
        private OrderTable orderTable;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder type(OrderType type) {
            this.type = type;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder orderDateTime(LocalDateTime orderDateTime) {
            this.orderDateTime = orderDateTime;
            return this;
        }

        public Builder addOrderLineItem(Menu menu, int quantity) {
            return addOrderLineItem(menu, quantity, menu.getPrice());
        }

        public Builder addOrderLineItem(Menu menu, int quantity, BigDecimal price) {
            OrderLineItem item = new OrderLineItem();
            item.setMenu(menu);
            item.setQuantity(quantity);
            item.setPrice(price);
            item.setMenuId(menu.getId());
            item.setSeq(FakeIdGenerator.get("orderLineItem"));
            this.orderLineItems.add(item);
            return this;
        }

        public Builder deliveryAddress(String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }

        public Builder orderTable(OrderTable orderTable) {
            this.orderTable = orderTable;
            return this;
        }

        @Override
        public Order build() {
            Order order = new Order();
            order.setId(this.id);
            order.setType(this.type);
            order.setStatus(this.status);
            order.setOrderDateTime(this.orderDateTime);
            order.setOrderLineItems(this.orderLineItems);
            order.setDeliveryAddress(this.deliveryAddress);
            if (!Objects.isNull(this.orderTable)) {
                order.setOrderTable(this.orderTable);
                order.setOrderTableId(this.orderTable.getId());
            }
            return order;
        }
    }


}
