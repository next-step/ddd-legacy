package kitchenpos.builder;

import kitchenpos.model.OrderTable;

public class OrderTableBuilder {
    private Long id;
    private Long tableGroupId;
    private int numberOfGuests;
    private boolean empty;

    private OrderTableBuilder() {}
    
    public static OrderTableBuilder orderTable() {
        return new OrderTableBuilder();
    }

    public OrderTableBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public OrderTableBuilder withTableGroupId(Long tableGroupId) {
        this.tableGroupId = tableGroupId;
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
        orderTable.setId(this.id);
        orderTable.setNumberOfGuests(this.numberOfGuests);
        orderTable.setTableGroupId(this.tableGroupId);
        orderTable.setEmpty(this.empty);
        return orderTable;
    }
}
