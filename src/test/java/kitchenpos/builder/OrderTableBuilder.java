package kitchenpos.builder;

import kitchenpos.model.OrderTable;

public class OrderTableBuilder {
    private Long id;
    private Long tableGroupId;
    private int numberOfGuests;
    private boolean empty;

    public OrderTableBuilder() {
    }

    public OrderTableBuilder id(Long val) {
        id = val;
        return this;
    }

    public OrderTableBuilder tableGroupId(Long val) {
        tableGroupId = val;
        return this;
    }

    public OrderTableBuilder numberOfGuests(int val) {
        numberOfGuests = val;
        return this;
    }

    public OrderTableBuilder empty(boolean val) {
        empty = val;
        return this;
    }

    public OrderTable build() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(this.id);
        orderTable.setTableGroupId(this.tableGroupId);
        orderTable.setNumberOfGuests(this.numberOfGuests);
        orderTable.setEmpty(this.empty);

        return orderTable;
    }

}
