package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderFailTestFixture {

    public static Order 아이템이_없는_주문() {
        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(UUID.randomUUID());
        order.setId(UUID.randomUUID());
        return order;
    }

    public static Order 상품_수량이_음수인_포장_주문(Menu menu) {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderLineItems(List.of(음수_상품(menu)));
        order.setOrderTableId(UUID.randomUUID());
        order.setId(UUID.randomUUID());
        return order;
    }

    public static Order 비활성화_메뉴_상품의_주문(Menu menu) {
        Order order = new Order();
        order.setOrderLineItems(List.of(주문_상품(menu)));
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(UUID.randomUUID());
        order.setId(UUID.randomUUID());
        return order;
    }

    public static Order 메뉴의_가격과_주문_아이템의_가격이_다른_주문(Menu menu) {
        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(List.of(가격이_다른_상품(menu)));
        order.setOrderTableId(UUID.randomUUID());
        order.setId(UUID.randomUUID());
        return order;
    }

    public static Order 주소가_없는_배달_주문(Menu menu) {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(주문_상품(menu)));
        order.setOrderTableId(UUID.randomUUID());
        order.setId(UUID.randomUUID());
        return order;
    }

    private static OrderLineItem 가격이_다른_상품(Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(3L);
        orderLineItem.setPrice(BigDecimal.valueOf(1));
        orderLineItem.setSeq(1L);
        orderLineItem.setMenuId(UUID.randomUUID());
        return orderLineItem;
    }

    private static OrderLineItem 주문_상품(Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(3L);
        orderLineItem.setPrice(menu.getPrice());
        orderLineItem.setSeq(1L);
        orderLineItem.setMenuId(UUID.randomUUID());
        return orderLineItem;
    }

    private static OrderLineItem 음수_상품(Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(-1);
        orderLineItem.setPrice(menu.getPrice());
        orderLineItem.setSeq(1L);
        orderLineItem.setMenuId(UUID.randomUUID());
        return orderLineItem;
    }


}


