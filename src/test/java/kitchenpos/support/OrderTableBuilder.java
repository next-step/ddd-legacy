package kitchenpos.support;

import kitchenpos.model.OrderTable;

public class OrderTableBuilder {
    public static final OrderTable EMPTY_ORDER_TABLE = OrderTableBuilder.orderTable().build();
    private Long id;
    private Long tableGroupId;
    private int numberOfGuests;
    private boolean empty;

    private OrderTableBuilder() {
    }

    public static OrderTableBuilder orderTable() {
        return new OrderTableBuilder();
    }

    public OrderTableBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public OrderTableBuilder withtTableGroupId(long tableGroupId) {
        this.tableGroupId = tableGroupId;
        return this;
    }

    public OrderTableBuilder withNoOfGuests(int numberOfGuests) {
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
        orderTable.setTableGroupId(tableGroupId);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);
        return orderTable;
    }
}
