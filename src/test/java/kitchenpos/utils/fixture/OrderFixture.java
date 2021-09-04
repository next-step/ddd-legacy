package kitchenpos.utils.fixture;

import kitchenpos.application.InMemoryOrderRepository;
import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static java.util.UUID.randomUUID;

public class OrderFixture {
    public static OrderRepository orderRepository = new InMemoryOrderRepository();

    public static OrderLineItem 주문항목() {
        final Menu menu = MenuFixture.메뉴저장();
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(2);
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }

    public static Order 주문() {
        final Order order = new Order();
        order.setOrderLineItems(new ArrayList<>(Arrays.asList(주문항목(), 주문항목())));
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        return order;
    }

    public static Order 배달주문() {
        final Order delivery = 주문();
        delivery.setType(OrderType.DELIVERY);
        delivery.setDeliveryAddress("배달주소");
        return delivery;
    }

    public static Order 포장주문() {
        final Order takeOut = 주문();
        takeOut.setType(OrderType.TAKEOUT);
        return takeOut;
    }

    public static Order 매장주문() {
        final Order eatIn = 주문();
        eatIn.setType(OrderType.EAT_IN);
        final OrderTable orderTable = OrderTableFixture.앉은테이블저장();
        eatIn.setOrderTable(orderTable);
        eatIn.setOrderTableId(orderTable.getId());
        return eatIn;
    }

    public static Order 주문저장(final Order order) {
        order.setId(randomUUID());
        return orderRepository.save(order);
    }

    public static void 비우기() {
        orderRepository = new InMemoryOrderRepository();
    }
}
