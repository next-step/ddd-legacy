package kitchenpos.application.fixture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public record OrderFixture(UUID id,
                           OrderType 주문유형,
                           OrderStatus 주문상태,
                           LocalDateTime 주문시간,
                           List<OrderLineItem> 주문아이템,
                           String 배달지주소,
                           OrderTable 주문테이블) {

    public static final OrderType DEFAULT_ORDER_TYPE = OrderType.DELIVERY;
    public static final OrderStatus DEFAULT_ORDER_STATUS = OrderStatus.WAITING;
    public static final String DEFAULT_DELIVERY_ADDRESS = "성남시 분당구 삼평동";
    public static final LocalDateTime DEFAULT_ORDER_TIME = LocalDateTime.now();

    public static OrderFixture init() {
        return new OrderFixture(
            UUID.randomUUID(),
            DEFAULT_ORDER_TYPE,
            DEFAULT_ORDER_STATUS,
            DEFAULT_ORDER_TIME,
            List.of(OrderLineItemFixture.init().create()),
            DEFAULT_DELIVERY_ADDRESS,
            OrderTableFixture.init().create()
        );
    }

    public static OrderFixture test(OrderType 주문유형,
        OrderStatus 주문상태,
        LocalDateTime 주문시간,
        List<OrderLineItem> 주문아이템,
        String 배달지주소,
        OrderTable 주문테이블) {
        return new OrderFixture(
            UUID.randomUUID(),
            주문유형,
            Objects.requireNonNullElse(주문상태, DEFAULT_ORDER_STATUS),
            Objects.requireNonNullElse(주문시간, DEFAULT_ORDER_TIME),
            주문아이템,
            배달지주소,
            주문테이블
        );
    }

    public Order create() {
        var order = new Order();
        order.setId(id);
        order.setType(주문유형);
        order.setStatus(주문상태);
        order.setOrderDateTime(주문시간);
        order.setOrderLineItems(주문아이템);
        order.setDeliveryAddress(배달지주소);
        order.setOrderTable(주문테이블);
        return order;
    }
}

