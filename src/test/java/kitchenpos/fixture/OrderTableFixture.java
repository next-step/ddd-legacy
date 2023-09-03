package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;


public class OrderTableFixture {

    public static OrderTable 왼쪽_테이블() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName("왼쪽 테이블 입니다");
        return orderTable;
    }

    public static OrderTable 착석_가능한_손님_2명의_오른쪽_테이블() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName("왼쪽 테이블 입니다");
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(2);
        return orderTable;
    }

    public static OrderTable 이름없는_테이블() {
        return new OrderTable();
    }
}
