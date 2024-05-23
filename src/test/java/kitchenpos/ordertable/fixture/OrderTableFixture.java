package kitchenpos.ordertable.fixture;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    public static final OrderTable A_테이블 = 테이블을_생성한다("A");

    public static final OrderTable B_테이블 = 테이블을_생성한다("B");

    public static final OrderTable 이름미존재_테이블 = 테이블을_생성한다(null);

    public static final OrderTable 빈문자이름_테이블 = 테이블을_생성한다("");

    private static OrderTable 테이블을_생성한다(String name) {
        return 테이블을_생성한다(name, 0, false);
    }

    private static OrderTable 테이블을_생성한다(String name, int numberOfGuests, boolean occupied) {
        var 테이블 = new OrderTable();
        테이블.setName(name);
        테이블.setNumberOfGuests(numberOfGuests);
        테이블.setOccupied(occupied);

        return 테이블;
    }

}
