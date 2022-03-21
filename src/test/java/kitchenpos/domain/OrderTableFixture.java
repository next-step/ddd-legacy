package kitchenpos.domain;

import java.util.UUID;

public class OrderTableFixture {

    public static final OrderTable TABLE_1_EMPTY =
        OrderTableFixture.builder()
                         .id(UUID.randomUUID())
                         .name("1번 테이블")
                         .empty(true)
                         .build();

    public static final OrderTable TABLE_1_NOT_EMPTY =
        OrderTableFixture.builder()
                         .id(TABLE_1_EMPTY.getId())
                         .name("1번 테이블")
                         .empty(false)
                         .numberOfGuests(5)
                         .build();

    public static final OrderTable TABLE_2_EMPTY =
        OrderTableFixture.builder()
                         .id(UUID.randomUUID())
                         .name("2번 테이블")
                         .empty(true)
                         .build();

    public static final OrderTable TABLE_3_NOT_EMPTY =
        OrderTableFixture.builder()
                         .id(UUID.randomUUID())
                         .name("3번 테이블")
                         .empty(false)
                         .numberOfGuests(10)
                         .build();

    private UUID id;
    private String name;
    private int numberOfGuests;
    private boolean empty;
    
    public static OrderTableFixture builder() {
        return new OrderTableFixture();
    }

    public OrderTableFixture id(UUID id) {
        this.id = id;
        return this;
    }

    public OrderTableFixture name(String name) {
        this.name = name;
        return this;
    }

    public OrderTableFixture numberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
        return this;
    }

    public OrderTableFixture empty(boolean empty) {
        this.empty = empty;
        return this;
    }

    public OrderTable build() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);
        return orderTable;
    }
}
