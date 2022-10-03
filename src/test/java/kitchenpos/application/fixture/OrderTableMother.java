package kitchenpos.application.fixture;

import kitchenpos.domain.OrderTable;

public class OrderTableMother {

	public static OrderTable create(String name) {
		OrderTable orderTable = new OrderTable();
		orderTable.setName(name);

		return orderTable;
	}
}
