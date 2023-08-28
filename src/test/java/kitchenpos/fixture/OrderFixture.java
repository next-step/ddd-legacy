package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.TEST_MENU;
import static kitchenpos.fixture.OrderTableFixture.TEST_ORDER_TABLE;

public class OrderFixture {

    public static Order TEST_ORDER_EAT_IN(OrderStatus orderStatus) {
        Order order = new Order();
        OrderTable orderTable = TEST_ORDER_TABLE();
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setType(OrderType.EAT_IN);
        order.setOrderDateTime(LocalDateTime.now());
        order.setId(UUID.randomUUID());
        order.setOrderLineItems(List.of(TEST_ORDER_LINE_ITEM()));
        order.setStatus(orderStatus);
        return order;
    }

    public static Order TEST_ORDER_DELIVERY(OrderStatus orderStatus) {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("여기로 배달와줘");
        order.setOrderDateTime(LocalDateTime.now());
        order.setId(UUID.randomUUID());
        order.setOrderLineItems(List.of(TEST_ORDER_LINE_ITEM()));
        order.setStatus(orderStatus);
        return order;
    }

    public static Order TEST_ORDER_TAKEOUT(OrderStatus orderStatus) {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderDateTime(LocalDateTime.now());
        order.setId(UUID.randomUUID());
        order.setOrderLineItems(List.of(TEST_ORDER_LINE_ITEM()));
        order.setStatus(orderStatus);
        return order;
    }

    public static OrderLineItem TEST_ORDER_LINE_ITEM() {
        OrderLineItem orderLineItem = new OrderLineItem();
        Menu menu = TEST_MENU();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(menu.getPrice());
        orderLineItem.setQuantity(1);
        return orderLineItem;
    }
}
