package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {
	public static OrderTable EMPTY_ORDER_TABLE() {
		OrderTable orderTable = new OrderTable();
		orderTable.setId(UUID.randomUUID());
		orderTable.setName("1번");
		orderTable.setNumberOfGuests(0);
		orderTable.setEmpty(true);
		return orderTable;
	}

	public static OrderTable SAT_ORDER_TABLE() {
		OrderTable orderTable = new OrderTable();
		orderTable.setId(UUID.randomUUID());
		orderTable.setName("1번");
		orderTable.setNumberOfGuests(0);
		orderTable.setEmpty(false);
		return orderTable;
	}
}
