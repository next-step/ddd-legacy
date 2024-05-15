package fixture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderFixture {
	private static final int VALID_ORDER_LINE_ITEM_QUANTITY = 1;
	private static final String VALID_ORDER_TABLE_NAME = "1번 테이블";

	public static OrderLineItem createOrderLineItem(Menu menu) {
		OrderLineItem validOrderLineItem = new OrderLineItem();
		validOrderLineItem.setMenu(menu);
		validOrderLineItem.setMenuId(menu.getId());
		validOrderLineItem.setQuantity(VALID_ORDER_LINE_ITEM_QUANTITY);
		validOrderLineItem.setPrice(menu.getPrice());
		return validOrderLineItem;
	}

	public static OrderTable createValidOrderTable() {
		UUID orderTableId = UUID.randomUUID();

		OrderTable validOrderTable = new OrderTable();
		validOrderTable.setId(orderTableId);
		validOrderTable.setName(VALID_ORDER_TABLE_NAME);
		validOrderTable.setOccupied(true);
		return validOrderTable;
	}

	public static Order createValidOrder(Menu menu) {
		OrderTable validOrderTable = createValidOrderTable();

		Order validOrder = new Order();
		validOrder.setId(UUID.randomUUID());
		validOrder.setType(OrderType.EAT_IN);
		validOrder.setOrderLineItems(List.of(createOrderLineItem(menu)));
		validOrder.setOrderDateTime(LocalDateTime.now());
		validOrder.setOrderTable(validOrderTable);
		validOrder.setOrderTableId(validOrderTable.getId());
		return validOrder;
	}
}
