package kitchenpos.order.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

import java.util.Collections;
import java.util.List;

import static kitchenpos.order.fixture.OrderLineItemFixture.봉골레_파스타_세트_메뉴_1개_주문;
import static kitchenpos.order.fixture.OrderLineItemFixture.봉골레_파스타_세트_메뉴_마이너스_1개_주문;
import static kitchenpos.order.fixture.OrderLineItemFixture.숨김처리된_메뉴_1개_주문;
import static kitchenpos.order.fixture.OrderLineItemFixture.주문_항목_가격_메뉴_가격_불일치;
import static kitchenpos.order.fixture.OrderLineItemFixture.토마토_파스타_단품_메뉴_1개_주문;

public class TakeoutOrderFixture {

    public static final Order 토마토_파스타_단품_메뉴_1개_테이크아웃주문 = 주문을_생성한다(
            List.of(토마토_파스타_단품_메뉴_1개_주문)
    );

    public static final Order 봉골레_세트_1개_토마토_단품_1개_테이크아웃주문 = 주문을_생성한다(
            List.of(봉골레_파스타_세트_메뉴_1개_주문, 토마토_파스타_단품_메뉴_1개_주문)
    );

    public static final Order 타입미존재_테이크아웃주문 = 주문을_생성한다(
            (OrderType) null,
            List.of(토마토_파스타_단품_메뉴_1개_주문)
    );

    public static final Order 주문항목미존재_테이크아웃주문 = 주문을_생성한다(null);

    public static final Order 빈_주문항목_테이크아웃주문 = 주문을_생성한다(Collections.emptyList());

    public static final Order 봉골레_세트_메뉴_마이너스_1개_테이크아웃주문 = 주문을_생성한다(
            List.of(봉골레_파스타_세트_메뉴_마이너스_1개_주문)
    );

    public static final Order 숨김처리된_메뉴_1개_테이크아웃주문 = 주문을_생성한다(
            List.of(숨김처리된_메뉴_1개_주문)
    );

    public static final Order 주문_항목_가격_메뉴_가격_불일치_테이크주문 = 주문을_생성한다(
            List.of(주문_항목_가격_메뉴_가격_불일치)
    );

    public static final Order 대기중인_메뉴_테이크아웃주문 = 주문을_생성한다(
            OrderStatus.WAITING,
            List.of(봉골레_파스타_세트_메뉴_1개_주문)
    );

    public static final Order 주문수락한_메뉴_테이크아웃주문 = 주문을_생성한다(
            OrderStatus.ACCEPTED,
            List.of(봉골레_파스타_세트_메뉴_1개_주문)
    );

    public static final Order 손님에게_전달한_메뉴_테이크아웃주문 = 주문을_생성한다(
            OrderStatus.SERVED,
            List.of(봉골레_파스타_세트_메뉴_1개_주문)
    );

    private static Order 주문을_생성한다(List<OrderLineItem> orderLineItems) {
        return 주문을_생성한다(OrderType.TAKEOUT, null, orderLineItems);
    }

    private static Order 주문을_생성한다(
            OrderType type,
            List<OrderLineItem> orderLineItems
    ) {
        return 주문을_생성한다(type, null, orderLineItems);
    }

    private static Order 주문을_생성한다(
            OrderStatus status,
            List<OrderLineItem> orderLineItems
    ) {
        return 주문을_생성한다(OrderType.TAKEOUT, status, orderLineItems);
    }

    private static Order 주문을_생성한다(
            OrderType type,
            OrderStatus status,
            List<OrderLineItem> orderLineItems
    ) {
        var 주문 = new Order();
        주문.setType(type);
        주문.setStatus(status);
        주문.setOrderLineItems(orderLineItems);

        return 주문;
    }

}
