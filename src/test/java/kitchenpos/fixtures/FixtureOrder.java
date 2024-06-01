package kitchenpos.fixtures;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class FixtureOrder {

    public static OrderTable fixtureOrderTable() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번");
        orderTable.setOccupied(false);
        orderTable.setNumberOfGuests(0);
        return orderTable;
    }

    public static OrderLineItem fixtureOrderLineItem() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        final Menu menu = FixtureMenu.fixtureMenu();
        orderLineItem.setSeq(1L);
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getMenuGroupId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(28_000L));
        return orderLineItem;
    }

    public static List<OrderLineItem> fixtureOrderLineItems() {
        final OrderLineItem orderLineItem = FixtureOrder.fixtureOrderLineItem();
        return List.of(orderLineItem);
    }

    public static Order fixtureOrder() {
        final Order order = new Order();
        final OrderTable orderTable = FixtureOrder.fixtureOrderTable();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(FixtureOrder.fixtureOrderLineItems());
        order.setDeliveryAddress("주소");
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        return order;
    }
}
