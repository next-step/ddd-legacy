package kitchenpos.application;

import kitchenpos.domain.OrderTable;

class OrderTableRequestBuilder {

    private String name = "테이블 이름";
    private int numberOfGuests = 0;
    private boolean occupied = false;

    public static OrderTableRequestBuilder builder() {
        return new OrderTableRequestBuilder();
    }

    public OrderTableRequestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public OrderTableRequestBuilder withNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
        return this;
    }

    public OrderTableRequestBuilder withOccupied(boolean occupied) {
        this.occupied = occupied;
        return this;
    }

    public OrderTable build() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }
}
