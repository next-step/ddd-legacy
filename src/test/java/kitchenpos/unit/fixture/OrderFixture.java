package kitchenpos.unit.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.*;
import static kitchenpos.unit.fixture.MenuFixture.*;
import static kitchenpos.unit.fixture.OrderTableFixture.테이블_1번;

public class OrderFixture {
    public static final String DELIVERY_ADDRESS = "서울시 마포구";
    public static Order 배달_주문;
    public static Order 포장_주문;
    public static Order 매장식사_주문;

    public static List<Menu> 배달_주문_메뉴_목록;
    public static List<Menu> 포장_주문_메뉴_목록;
    public static List<Menu> 매장식사_주문_메뉴_목록;

    static {
        배달_주문_메뉴_목록 = Arrays.asList(한그릇_세트);
        포장_주문_메뉴_목록 = Arrays.asList(한그릇_세트, 두그릇_세트);
        매장식사_주문_메뉴_목록 = Arrays.asList(한그릇_세트, 두그릇_세트, 세그릇_세트);

        배달_주문 = createOrderWithMenus(DELIVERY, DELIVERY_ADDRESS, null, 배달_주문_메뉴_목록, WAITING);
        포장_주문 = createOrderWithMenus(TAKEOUT, null, null, 포장_주문_메뉴_목록, WAITING);
        매장식사_주문 = createOrderWithMenus(EAT_IN, null, 테이블_1번, 매장식사_주문_메뉴_목록, WAITING);
    }

    public static Order createOrderWithMenus(OrderType orderType, String deliveryAddress, OrderTable orderTable, List<Menu> menus, OrderStatus status) {
        return createOrder(orderType, deliveryAddress, orderTable, createOrderLineItems(menus), status);
    }

    public static Order createOrder(OrderType orderType, String deliveryAddress, OrderTable orderTable, List<OrderLineItem> orderLineItems, OrderStatus status) {
        Order order = new Order();
        order.setType(orderType);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(status);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static List<OrderLineItem> createOrderLineItems(List<Menu> menus) {
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        menus.forEach(m -> {
            orderLineItems.add(createOrderLineItem(m, 1L));
        });
        return orderLineItems;
    }

    public static OrderLineItem createOrderLineItem(Menu menu, long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(menu.getPrice());
        orderLineItem.setMenuId(menu.getId());
        return  orderLineItem;
    }
}
