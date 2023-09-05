package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DeliveryOrderFixture {

    public static Order 배달_주문(Menu menu) {
        Order order = new Order();
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(배달_주문_상품(menu)));
        order.setType(OrderType.DELIVERY);
        order.setId(UUID.randomUUID());
        order.setDeliveryAddress("구의동 257-90 1005호");
        return order;
    }

    public static Order 대기중인_배달주문(Menu menu) {
        Order order = new Order();
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(배달_주문_상품(menu)));
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        order.setId(UUID.randomUUID());
        order.setDeliveryAddress("구의동 257-90 1005호");
        return order;
    }

    public static Order 수락된_배달_주문(Menu menu) {
        Order order = new Order();
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(배달_주문_상품(menu)));
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.ACCEPTED);
        order.setId(UUID.randomUUID());
        order.setDeliveryAddress("구의동 257-90 1005호");
        return order;
    }


    public static Order 배달_준비된_주문(Menu menu) {
        Order order = new Order();
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(배달_주문_상품(menu)));
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.SERVED);
        order.setId(UUID.randomUUID());
        order.setDeliveryAddress("구의동 257-90 1005호");
        return order;
    }

    public static Order 배달중인_주문(Menu menu) {
        Order order = new Order();
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(배달_주문_상품(menu)));
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERING);
        order.setId(UUID.randomUUID());
        order.setDeliveryAddress("구의동 257-90 1005호");
        return order;
    }

    public static Order 배달이_완료된_주문(Menu menu) {
        Order order = new Order();
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(배달_주문_상품(menu)));
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERED);
        order.setId(UUID.randomUUID());
        order.setDeliveryAddress("구의동 257-90 1005호");
        return order;
    }


    private static OrderLineItem 배달_주문_상품(Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(3L);
        orderLineItem.setPrice(menu.getPrice());
        orderLineItem.setSeq(1L);
        orderLineItem.setMenuId(UUID.randomUUID());
        return orderLineItem;
    }
}
