package kitchenpos.order.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {
    public OrderTable 주문_테이블_A = create("주문_테이블_A", 5);
    public OrderTable 주문_테이블_B = create("주문_테이블_B", 7);
    public OrderTable 손님_있는_주문_테이블 = create("손님_있는_주문_테이블", 5, true);
    public OrderTable 이름_없는_주문_테이블 = create(null, 5);
    public OrderTable 손님_음수_주문_테이블 = create("주문_테이블", -1);

    public static OrderTable create(String name, int numberOfGuest) {
        return create(UUID.randomUUID(), name, numberOfGuest, false);
    }

    public static OrderTable create(String name, int numberOfQuest, boolean occupied) {
        return create(UUID.randomUUID(), name, numberOfQuest, occupied);
    }

    public static OrderTable create(UUID id, String name, int numberOfGuest, boolean occupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuest);
        orderTable.setOccupied(occupied);

        return orderTable;
    }
}