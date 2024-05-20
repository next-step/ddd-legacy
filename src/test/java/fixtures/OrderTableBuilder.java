package fixtures;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableBuilder {

    private UUID id = UUID.randomUUID();
    private String name = "테이블";
    private int numberOfGuests = 0;
    private boolean occupied = true;

    public OrderTableBuilder anOrderTable() {
        return new OrderTableBuilder();
    }


    public OrderTableBuilder with(String name) {
        this.name = name;
        return this;
    }

    public OrderTableBuilder withOccupied(boolean occupied) {
        this.occupied = occupied;
        return this;
    }

    public OrderTableBuilder withNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
        return this;
    }


    public OrderTable build() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }
}
