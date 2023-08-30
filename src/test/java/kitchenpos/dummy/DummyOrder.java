package kitchenpos.dummy;

import kitchenpos.domain.*;

import java.util.List;
import java.util.UUID;

public class DummyOrder {

    public static Order createOrder(OrderTable orderTable, List<OrderLineItem> orderLineItems) {
        return createOrder(OrderType.EAT_IN, orderTable, orderLineItems);
    }

    public static Order createOrder(OrderType orderType, OrderTable orderTable, List<OrderLineItem> orderLineItems) {
        Order request = new Order();
        request.setId(UUID.randomUUID());
        request.setType(orderType);
        request.setOrderLineItems(orderLineItems);
        request.setOrderTable(orderTable);
        request.setOrderTableId(orderTable.getId());
        if (orderType.equals(OrderType.DELIVERY)) {
            request.setDeliveryAddress("서울시 강남구");
        }
        return request;
    }

    public static List<OrderLineItem> createOrderLineItem(Menu menu1, Menu menu2) {
        OrderLineItem orderLineItem1 = new OrderLineItem();
        orderLineItem1.setSeq(1L);
        orderLineItem1.setMenu(menu1);
        orderLineItem1.setMenuId(menu1.getId());
        orderLineItem1.setPrice(menu1.getPrice());
        orderLineItem1.setQuantity(1L);
        OrderLineItem orderLineItem2 = new OrderLineItem();
        orderLineItem2.setSeq(2L);
        orderLineItem2.setMenu(menu2);
        orderLineItem2.setMenuId(menu2.getId());
        orderLineItem2.setPrice(menu2.getPrice());
        orderLineItem2.setQuantity(2L);

        List<OrderLineItem> orderLineItems = List.of(orderLineItem1, orderLineItem2);
        return orderLineItems;
    }


}
