package kitchenpos.application;

import static kitchenpos.application.MenuServiceFixture.menu;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.graalvm.compiler.core.common.type.ArithmeticOpTable.BinaryOp.Or;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

public final class OrderServiceFixture {

    private OrderServiceFixture() {

    }

    public static Order order() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        return order;
    }

    public static List<Order> orders() {
        List<Order> orders = new ArrayList<>();
        orders.add(order());
        return orders;
    }

    public static OrderLineItem orderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        Menu menu = menu();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(menu.getPrice());
        orderLineItem.setQuantity(1);
        return orderLineItem;
    }

    public static List<OrderLineItem> orderLineItems() {
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem());
        return orderLineItems;
    }
}
