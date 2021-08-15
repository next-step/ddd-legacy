package kitchenpos.builder;

import kitchenpos.domain.OrderTable;

import java.util.Random;
import java.util.UUID;

public final class OrderTableBuilder {
    private UUID id;
    private String name;
    private int numberOfGuests;
    private boolean empty;

    private OrderTableBuilder() {
        id = UUID.randomUUID();
        name = new Random().nextInt(9) + 1 + "번 테이블";
        numberOfGuests = 0;
        empty = true;
    }

    public static OrderTableBuilder anOrderTable() {
        return new OrderTableBuilder();
    }

    public OrderTableBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public OrderTableBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public OrderTableBuilder setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
        return this;
    }

    public OrderTableBuilder setEmpty(boolean empty) {
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
