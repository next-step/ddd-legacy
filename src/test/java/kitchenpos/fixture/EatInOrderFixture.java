package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EatInOrderFixture {

    public static Order 매장_주문(Menu menu) {
        Order order = new Order();
        order.setOrderTableId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(매장_주문_상품(menu)));
        order.setType(OrderType.EAT_IN);
        order.setId(UUID.randomUUID());
        order.setDeliveryAddress("구의동 257-90 1005호");
        return order;
    }

    public static Order 대기중인_매장주문(Menu menu) {
        Order order = new Order();
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(매장_주문_상품(menu)));
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.WAITING);
        order.setId(UUID.randomUUID());
        order.setDeliveryAddress("구의동 257-90 1005호");
        return order;
    }

    public static Order 수락된_매장_주문(Menu menu) {
        Order order = new Order();
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(매장_주문_상품(menu)));
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.ACCEPTED);
        order.setId(UUID.randomUUID());
        order.setDeliveryAddress("구의동 257-90 1005호");
        return order;
    }


    public static Order 매장_준비된_주문(Menu menu, OrderTable orderTable) {
        Order order = new Order();
        order.setOrderTableId(UUID.randomUUID());
        order.setOrderTable(orderTable);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(매장_주문_상품(menu)));
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.SERVED);
        order.setId(UUID.randomUUID());
        order.setDeliveryAddress("구의동 257-90 1005호");
        return order;
    }


    private static OrderLineItem 매장_주문_상품(Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(3L);
        orderLineItem.setPrice(menu.getPrice());
        orderLineItem.setSeq(1L);
        orderLineItem.setMenuId(UUID.randomUUID());
        return orderLineItem;
    }
}
