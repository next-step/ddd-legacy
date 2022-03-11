package kitchenpos.testBuilders;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableBuilder {
	public static final int DEFAULT_NUMBER_OF_GUESTS = 3;
	private UUID id;
	private String name;
	private int numberOfGuests;
	private boolean empty;

	private OrderTableBuilder() {
	}

	public static OrderTableBuilder aOrderTable() {
		return new OrderTableBuilder();
	}

	public static OrderTableBuilder aDefaultOrderTable() {
		return aEmptyOrderTable();
	}

	public static OrderTableBuilder aEmptyOrderTable() {
		UUID id = UUID.randomUUID();
		return aOrderTable()
				.withId(id)
				.withName(id + "번 테이블")
				.withEmpty(true)
				.withNumberOfGuests(0);
	}

	public static OrderTableBuilder aNotEmptyOrderTable() {
		UUID id = UUID.randomUUID();
		return aOrderTable()
				.withId(id)
				.withName(id + "번 테이블")
				.withEmpty(false)
				.withNumberOfGuests(DEFAULT_NUMBER_OF_GUESTS);
	}

	public static OrderTableBuilder aOrderTableByEmpty(boolean empty) {
		return empty ? aEmptyOrderTable() : aNotEmptyOrderTable();
	}

	public OrderTableBuilder withId(UUID id) {
		this.id = id;
		return this;
	}

	public OrderTableBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public OrderTableBuilder withNumberOfGuests(int numberOfGuests) {
		this.numberOfGuests = numberOfGuests;
		return this;
	}

	public OrderTableBuilder withEmpty(boolean empty) {
		this.empty = empty;
		return this;
	}

	public OrderTable build() {
		OrderTable orderTable = new OrderTable();
		orderTable.setId(id);
		orderTable.setName(name);
		orderTable.setNumberOfGuests(numberOfGuests);
		orderTable.setEmpty(empty);
		return orderTable;
	}
}
