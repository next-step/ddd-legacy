package kitchenpos.testHelper.fixture;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    public static OrderTableCreateRequestBuilder createRequestBuilder() {
        return new OrderTableCreateRequestBuilder();
    }

    public static class OrderTableCreateRequestBuilder {

        private String name;

        public OrderTableCreateRequestBuilder name(final String name) {
            this.name = name;

            return this;
        }

        public OrderTable build() {
            OrderTable orderTable = new OrderTable();
            orderTable.setName(name);

            return orderTable;
        }
    }

    public static OrderTableUpdateRequestBuilder updateRequestBuilder() {
        return new OrderTableUpdateRequestBuilder();
    }

    public static class OrderTableUpdateRequestBuilder {

        private int numberOfGuests;

        public OrderTableUpdateRequestBuilder numberOfGuests(final int numberOfGuests) {
            this.numberOfGuests = numberOfGuests;

            return this;
        }

        public OrderTable build() {
            OrderTable orderTable = new OrderTable();
            orderTable.setNumberOfGuests(numberOfGuests);

            return orderTable;
        }
    }
}
