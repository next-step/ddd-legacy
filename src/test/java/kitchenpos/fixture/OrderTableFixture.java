package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    public static class OrderTableBuilder {
        private UUID id;
        private String name;
        private int numberOfGuests;
        private boolean empty;

        public OrderTableBuilder() {
            this.id = UUID.randomUUID();
        }

        public OrderTableBuilder name(String name) {
            this.name=name;
            return this;
        }

        public OrderTableBuilder numberOfGuests(int numberOfGuests) {
            this.numberOfGuests=numberOfGuests;
            return this;
        }

        public OrderTableBuilder empty(boolean empty) {
            this.empty=empty;
            return this;
        }

        public OrderTable build() {
            OrderTable orderTable = new OrderTable();
            orderTable.setId(this.id);
            orderTable.setName(this.name);
            orderTable.setNumberOfGuests(this.numberOfGuests);
            orderTable.setEmpty(this.empty);
            return orderTable;
        }
    }
}
