package kitchenpos.domain;

import java.util.UUID;

public class OrderTableFixture {
    private UUID id;
    private String name;
    private int numberOfGuests;
    private boolean empty;
    
    public static OrderTableFixture builder() {
        return new OrderTableFixture();
    }

    public OrderTableFixture id(UUID id) {
        this.id = id;
        return this;
    }

    public OrderTableFixture name(String name) {
        this.name = name;
        return this;
    }

    public OrderTableFixture numberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
        return this;
    }

    public OrderTableFixture empty(boolean empty) {
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
