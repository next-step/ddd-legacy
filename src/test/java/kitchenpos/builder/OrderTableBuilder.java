package kitchenpos.builder;

import kitchenpos.model.OrderTable;

public class OrderTableBuilder {
    private Long id;
    private Long tableGroupId;
    private int numberOfGuests;
    private boolean empty;

    public OrderTableBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public OrderTableBuilder setTableGroupId(Long tableGroupId) {
        this.tableGroupId = tableGroupId;
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
        orderTable.setEmpty(empty);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setTableGroupId(tableGroupId);

        return orderTable;
    }
}
