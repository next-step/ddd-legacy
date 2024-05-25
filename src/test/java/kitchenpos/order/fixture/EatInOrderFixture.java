package kitchenpos.order.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

import java.util.Collections;
import java.util.List;

import static kitchenpos.order.fixture.OrderLineItemFixture.김치찜_1인_메뉴_1개_주문;
import static kitchenpos.order.fixture.OrderLineItemFixture.봉골레_파스타_세트_메뉴_1개_주문;
import static kitchenpos.order.fixture.OrderLineItemFixture.봉골레_파스타_세트_메뉴_마이너스_1개_주문;
import static kitchenpos.order.fixture.OrderLineItemFixture.숨김처리된_메뉴_1개_주문;
import static kitchenpos.order.fixture.OrderLineItemFixture.주문_항목_가격_메뉴_가격_불일치;
import static kitchenpos.order.fixture.OrderLineItemFixture.토마토_파스타_단품_메뉴_1개_주문;
import static kitchenpos.ordertable.fixture.OrderTableFixture.점유하고있는_테이블_1;
import static kitchenpos.ordertable.fixture.OrderTableFixture.점유하고있는_테이블_2;
import static kitchenpos.ordertable.fixture.OrderTableFixture.점유하고있는_테이블_3;
import static kitchenpos.ordertable.fixture.OrderTableFixture.점유하지_않고_있는_테이블_1;

public class EatInOrderFixture {

    public static final Order 김치찜_1인_메뉴_1개_매장주문 = 주문을_생성한다(
            List.of(김치찜_1인_메뉴_1개_주문),
            점유하고있는_테이블_1
    );

    public static final Order 봉골레_세트_1개_토마토_단품_1개_매장주문 = 주문을_생성한다(
            List.of(봉골레_파스타_세트_메뉴_1개_주문, 토마토_파스타_단품_메뉴_1개_주문),
            점유하고있는_테이블_1
    );

    public static final Order 타입미존재_매장주문 = 주문을_생성한다(
            (OrderType) null,
            List.of(김치찜_1인_메뉴_1개_주문),
            점유하고있는_테이블_1
    );

    public static final Order 주문항목미존재_매장주문 = 주문을_생성한다(
            null,
            점유하고있는_테이블_1
    );

    public static final Order 빈_주문항목_매장주문 = 주문을_생성한다(
            Collections.emptyList(),
            점유하고있는_테이블_1
    );

    public static final Order 봉골레_세트_메뉴_마이너스_1개_매장주문 = 주문을_생성한다(
            List.of(봉골레_파스타_세트_메뉴_마이너스_1개_주문),
            점유하고있는_테이블_1
    );

    public static final Order 숨김처리된_메뉴_1개_매장주문 = 주문을_생성한다(
            List.of(숨김처리된_메뉴_1개_주문),
            점유하고있는_테이블_1
    );

    public static final Order 주문_항목_가격_메뉴_가격_불일치_매장주문 = 주문을_생성한다(
            List.of(주문_항목_가격_메뉴_가격_불일치),
            점유하고있는_테이블_1
    );

    public static final Order 점유하지않고_봉골레_파스타_세트_메뉴_1개_매장주문 = 주문을_생성한다(
            List.of(봉골레_파스타_세트_메뉴_1개_주문),
            점유하지_않고_있는_테이블_1
    );

    public static final Order 대기중인_메뉴_매장주문 = 주문을_생성한다(
            OrderStatus.WAITING,
            List.of(토마토_파스타_단품_메뉴_1개_주문),
            점유하고있는_테이블_2
    );

    public static final Order 주문수락한_메뉴_매장주문 = 주문을_생성한다(
            OrderStatus.ACCEPTED,
            List.of(토마토_파스타_단품_메뉴_1개_주문),
            점유하고있는_테이블_2
    );

    public static final Order 손님에게_전달한_메뉴_매장주문_식사완료 = 주문을_생성한다(
            OrderStatus.SERVED,
            List.of(토마토_파스타_단품_메뉴_1개_주문),
            점유하고있는_테이블_2
    );

    public static final Order 손님에게_전달한_메뉴_매장주문_식사진행중 = 주문을_생성한다(
            OrderStatus.SERVED,
            List.of(토마토_파스타_단품_메뉴_1개_주문),
            점유하고있는_테이블_3
    );

    private static Order 주문을_생성한다(
            List<OrderLineItem> orderLineItems,
            OrderTable orderTable
    ) {
        return 주문을_생성한다(OrderType.EAT_IN, null, orderLineItems, orderTable);
    }

    private static Order 주문을_생성한다(
            OrderType type,
            List<OrderLineItem> orderLineItems,
            OrderTable orderTable
    ) {
        return 주문을_생성한다(type, null, orderLineItems, orderTable);
    }

    private static Order 주문을_생성한다(
            OrderStatus status,
            List<OrderLineItem> orderLineItems,
            OrderTable orderTable
    ) {
        return 주문을_생성한다(OrderType.EAT_IN, status, orderLineItems, orderTable);
    }

    private static Order 주문을_생성한다(
            OrderType type,
            OrderStatus status,
            List<OrderLineItem> orderLineItems,
            OrderTable orderTable
    ) {
        var 주문 = new Order();
        주문.setType(type);
        주문.setStatus(status);
        주문.setOrderLineItems(orderLineItems);
        주문.setOrderTableId(orderTable.getId());
        주문.setOrderTable(orderTable);

        return 주문;
    }

}
