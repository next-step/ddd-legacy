package kitchenpos.order.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static kitchenpos.order.fixture.OrderLineItemFixture.봉골레_파스타_세트_메뉴_1개_주문;
import static kitchenpos.order.fixture.OrderLineItemFixture.봉골레_파스타_세트_메뉴_마이너스_1개_주문;
import static kitchenpos.order.fixture.OrderLineItemFixture.숨김처리된_메뉴_1개_주문;
import static kitchenpos.order.fixture.OrderLineItemFixture.주문_항목_가격_메뉴_가격_불일치;
import static kitchenpos.order.fixture.OrderLineItemFixture.토마토_파스타_단품_메뉴_1개_주문;


public class DeliveryOrderFixture {

    public static final Order 봉골레_세트_메뉴_1개_배달주문 = 주문을_생성한다(
            List.of(봉골레_파스타_세트_메뉴_1개_주문),
            "서울 강서구"
    );

    public static final Order 봉골레_세트_1개_토마토_단품_1개_배달주문 = 주문을_생성한다(
            List.of(봉골레_파스타_세트_메뉴_1개_주문, 토마토_파스타_단품_메뉴_1개_주문),
            "서울 마포구"
    );

    public static final Order 타입미존재_배달주문 = 주문을_생성한다(
            (OrderType) null,
            List.of(봉골레_파스타_세트_메뉴_1개_주문),
            "서울 양천구"
    );

    public static final Order 주문항목미존재_배달주문 = 주문을_생성한다(
            null,
            "서울 구로구"
    );

    public static final Order 빈_주문항목_배달주문 = 주문을_생성한다(
            Collections.emptyList(),
            "서울 영등포구"
    );

    public static final Order 봉골레_세트_메뉴_마이너스_1개_배달주문 = 주문을_생성한다(
            List.of(봉골레_파스타_세트_메뉴_마이너스_1개_주문),
            "서울 영등포구"
    );

    public static final Order 숨김처리된_메뉴_1개_배달주문 = 주문을_생성한다(
            List.of(숨김처리된_메뉴_1개_주문),
            "서울 서대문구"
    );

    public static final Order 주문_항목_가격_메뉴_가격_불일치_배달주문 = 주문을_생성한다(
            List.of(주문_항목_가격_메뉴_가격_불일치),
            "서울 은평구"
    );

    public static final Order 배달주소미존재_배달주문 = 주문을_생성한다(
            List.of(토마토_파스타_단품_메뉴_1개_주문),
            null
    );

    public static final Order 빈문자배달주소_배달주문 = 주문을_생성한다(
            List.of(토마토_파스타_단품_메뉴_1개_주문),
            ""
    );

    private static Order 주문을_생성한다(
            List<OrderLineItem> orderLineItems,
            String deliveryAddress
    ) {
        return 주문을_생성한다(null, OrderType.DELIVERY, null, orderLineItems, deliveryAddress);
    }

    private static Order 주문을_생성한다(
            OrderType type,
            List<OrderLineItem> orderLineItems,
            String deliveryAddress
    ) {
        return 주문을_생성한다(null, type, null, orderLineItems, deliveryAddress);
    }

    private static Order 주문을_생성한다(
            OrderStatus status,
            List<OrderLineItem> orderLineItems,
            String deliveryAddress
    ) {
        return 주문을_생성한다(null, OrderType.DELIVERY, status, orderLineItems, deliveryAddress);
    }

    private static Order 주문을_생성한다(
            OrderType type,
            OrderStatus status,
            List<OrderLineItem> orderLineItems,
            String deliveryAddress
    ) {
        return 주문을_생성한다(null, type, status, orderLineItems, deliveryAddress);
    }

    private static Order 주문을_생성한다(
            UUID id,
            OrderType type,
            OrderStatus status,
            List<OrderLineItem> orderLineItems,
            String deliveryAddress
    ) {
        var 주문 = new Order();
        주문.setId(id);
        주문.setType(type);
        주문.setStatus(status);
        주문.setOrderDateTime(LocalDateTime.now());
        주문.setOrderLineItems(orderLineItems);
        주문.setDeliveryAddress(deliveryAddress);

        return 주문;
    }

}
