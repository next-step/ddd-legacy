package kitchenpos.support;

import kitchenpos.model.OrderTable;

public class OrderTableBuilder {
    private Long id;
    private Long tableGroupId;
    private int numberOfGuests;
    private boolean empty;

    public OrderTableBuilder id (Long id){
        this.id = id;
        return this;
    }

    public OrderTableBuilder tableGroupId (Long tableGroupId){
        this.tableGroupId = tableGroupId;
        return this;
    }

    public OrderTableBuilder numberOfGuests (int numberOfGuests){
        this.numberOfGuests = numberOfGuests;
        return this;
    }

    public OrderTableBuilder empty (boolean empty){
        this.empty = empty;
        return this;
    }

    public OrderTable build(){
        OrderTable orderTable = new OrderTable();
        orderTable.setId(this.id);
        orderTable.setTableGroupId(this.tableGroupId);
        orderTable.setNumberOfGuests(this.numberOfGuests);
        orderTable.setEmpty(this.empty);

        return orderTable;
    }
}
