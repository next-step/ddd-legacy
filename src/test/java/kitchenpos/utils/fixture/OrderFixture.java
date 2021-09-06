package kitchenpos.utils.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static java.util.UUID.randomUUID;

public class OrderFixture {

    public static OrderLineItem 주문항목(Menu menu) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(2L);
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }

    public static Order 주문(Menu menu) {
        final Order order = new Order();
        order.setId(randomUUID());
        order.setOrderLineItems(new ArrayList<>(Arrays.asList(주문항목(menu))));
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        return order;
    }

    public static Order 배달주문(Menu menu) {
        final Order delivery = 주문(menu);
        delivery.setType(OrderType.DELIVERY);
        delivery.setDeliveryAddress("배달주소");
        return delivery;
    }

    public static Order 포장주문(Menu menu) {
        final Order takeOut = 주문(menu);
        takeOut.setType(OrderType.TAKEOUT);
        return takeOut;
    }

    public static Order 매장주문(Menu menu, OrderTable orderTable) {
        final Order eatIn = 주문(menu);
        eatIn.setType(OrderType.EAT_IN);
        eatIn.setOrderTable(orderTable);
        eatIn.setOrderTableId(orderTable.getId());
        return eatIn;
    }

}
