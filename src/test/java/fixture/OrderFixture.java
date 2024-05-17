package fixture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderFixture {
	public static final String VALID_ORDER_TABLE_NAME = "1번 테이블";

	private static final int VALID_ORDER_LINE_ITEM_QUANTITY = 1;

	public static OrderTable createOrderTable(String name, boolean occupied, int numberOfGuests) {
		OrderTable orderTable = new OrderTable();
		orderTable.setId(UUID.randomUUID());
		orderTable.setName(name);
		orderTable.setOccupied(occupied);
		orderTable.setNumberOfGuests(numberOfGuests);
		return orderTable;
	}

	public static OrderTable createValidOrderTable() {
		return createOrderTable(VALID_ORDER_TABLE_NAME, false, 0);
	}

	public static OrderLineItem createOrderLineItem(Menu menu, int quantity) {
		OrderLineItem orderLineItem = new OrderLineItem();
		orderLineItem.setMenu(menu);
		orderLineItem.setMenuId(menu.getId());
		orderLineItem.setQuantity(quantity);
		orderLineItem.setPrice(menu.getPrice());
		return orderLineItem;
	}

	public static OrderLineItem createValidOrderLineItem() {
		return createOrderLineItem(MenuFixture.createValid(), VALID_ORDER_LINE_ITEM_QUANTITY);
	}

	public static Order create(
		List<OrderLineItem> orderLineItems,
		OrderTable orderTable,
		OrderType type,
		OrderStatus orderStatus) {
		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setOrderLineItems(orderLineItems);
		order.setOrderDateTime(LocalDateTime.now());
		order.setOrderTable(orderTable);
		order.setOrderTableId(orderTable.getId());
		order.setType(type);
		order.setStatus(orderStatus);
		return order;
	}

	public static Order createValid() {
		return create(List.of(createValidOrderLineItem()), createValidOrderTable(), OrderType.EAT_IN,
			OrderStatus.WAITING);
	}

	public static Order createValidWithTypeAndStatus(OrderType orderType, OrderStatus orderStatus) {
		return create(List.of(createValidOrderLineItem()), createValidOrderTable(), orderType, orderStatus);
	}
}
