package kitchenpos.application;

import static kitchenpos.application.MenuFixture.맛초킹_세트;
import static kitchenpos.application.MenuFixture.뿌링클_세트;
import static kitchenpos.application.OrderTableFixture.일번_테이블;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;

public class OrderFixture {

    public static final Order 매장_주문 = new Order();
    public static final Order 포장_주문 = new Order();
    public static final Order 배달_주문 = new Order();

    static {
        매장_주문.setId(UUID.randomUUID());
        매장_주문.setType(OrderType.EAT_IN);
        매장_주문.setOrderLineItems(Collections.singletonList(주문_항목_뿌링클_세트()));
        매장_주문.setOrderTable(일번_테이블);
        매장_주문.setOrderTableId(일번_테이블.getId());

        포장_주문.setId(UUID.randomUUID());
        포장_주문.setType(OrderType.TAKEOUT);
        포장_주문.setOrderLineItems(Collections.singletonList(주문_항목_맛초킹_세트()));

        배달_주문.setId(UUID.randomUUID());
        배달_주문.setType(OrderType.DELIVERY);
        배달_주문.setOrderLineItems(Arrays.asList(주문_항목_뿌링클_세트(), 주문_항목_맛초킹_세트()));
        배달_주문.setDeliveryAddress("ADDRESS");
    }

    private static OrderLineItem 주문_항목_뿌링클_세트() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(뿌링클_세트);
        return orderLineItem;
    }

    private static OrderLineItem 주문_항목_맛초킹_세트() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(맛초킹_세트);
        return orderLineItem;
    }
}
