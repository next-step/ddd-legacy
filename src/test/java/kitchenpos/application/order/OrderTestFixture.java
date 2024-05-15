package kitchenpos.application.order;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderTestFixture {

    public static OrderTable aOrderTable(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(false);
        return orderTable;
    }

    public static OrderTable aOrderTable(String name, boolean occupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(occupied);
        return orderTable;
    }

    public static OrderTable aOrderTableWithGuests(String name, int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(false);
        return orderTable;
    }

    public static Order aOrder(
            OrderTable orderTable,
            OrderType orderType,
            OrderStatus orderStatus,
            List<OrderLineItem> orderLineItems,
            String deliveryAddress
    ) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        return order;
    }

    public static OrderLineItem aOrderLineItem(Menu menu, long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }

}
