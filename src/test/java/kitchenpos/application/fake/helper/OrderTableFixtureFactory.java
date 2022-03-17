package kitchenpos.application.fake.helper;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public final class OrderTableFixtureFactory {

    public static final OrderTable 오션뷰_테이블_01 = new Builder()
            .id(UUID.randomUUID())
            .name("오션뷰 테이블 01")
            .build();


    public static final OrderTable 오션뷰_테이블_02_이용중 = new Builder()
            .id(UUID.randomUUID())
            .name("오션뷰 테이블 02")
            .empty(false)
            .numberOfGuests(1)
            .build();

    public static final class Builder implements FixtureBuilder<OrderTable> {

        private UUID id;
        private String name;
        private int numberOfGuests = 0;
        private boolean empty = true;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder numberOfGuests(int numberOfGuests) {
            this.numberOfGuests = numberOfGuests;
            return this;
        }

        public Builder empty(boolean empty) {
            this.empty = empty;
            return this;
        }

        @Override
        public OrderTable build() {
            OrderTable table = new OrderTable();
            table.setId(this.id);
            table.setName(this.name);
            table.setNumberOfGuests(this.numberOfGuests);
            table.setEmpty(this.empty);
            return table;
        }

    }


}
