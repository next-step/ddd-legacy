package kitchenpos.model;

public class OrderTable {
    private Long id;
    private Long tableGroupId;
    private int numberOfGuests;
    private boolean empty;

    private OrderTable (Builder builder){
        this.id = builder.id;
        this.tableGroupId = builder.tableGroupId;
        this.numberOfGuests = builder.numberOfGuests;
        this.empty = builder.empty;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getTableGroupId() {
        return tableGroupId;
    }

    public void setTableGroupId(final Long tableGroupId) {
        this.tableGroupId = tableGroupId;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(final int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(final boolean empty) {
        this.empty = empty;
    }

    public static class Builder {
        private Long id;
        private Long tableGroupId;
        private int numberOfGuests;
        private boolean empty;

        public Builder id (Long id){
            this.id = id;
            return this;
        }

        public Builder tableGroupId(Long tableGroupId){
            this.tableGroupId = tableGroupId;
            return this;
        }

        public Builder numberOfGuests (int numberOfGuests){
            this.numberOfGuests = numberOfGuests;
            return this;
        }

        public Builder empty(boolean empty){
            this.empty = empty;
            return this;
        }

        public OrderTable build(){
            return new OrderTable(this);
        }
    }
}
