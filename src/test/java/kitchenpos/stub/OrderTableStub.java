package kitchenpos.stub;

import java.util.UUID;

import kitchenpos.domain.OrderTable;

public class OrderTableStub {

	private static final String DEFAULT_NAME = "기본 테이블";

	private OrderTableStub() {
	}

	public static OrderTable createDefault() {
		return createCustom(DEFAULT_NAME, false, 0);
	}

	public static OrderTable createCustom(String name, boolean occupied, int numberOfGuests) {
		OrderTable orderTable = new OrderTable();
		orderTable.setId(UUID.randomUUID());
		orderTable.setName(name);
		orderTable.setOccupied(occupied);
		orderTable.setNumberOfGuests(numberOfGuests);
		return orderTable;
	}
}
