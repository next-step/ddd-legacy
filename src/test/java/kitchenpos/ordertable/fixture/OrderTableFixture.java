package kitchenpos.ordertable.fixture;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    public static final OrderTable 테이블_1 = 테이블을_생성한다("1");

    public static final OrderTable 테이블_2 = 테이블을_생성한다("2");

    public static final OrderTable 이름미존재_테이블 = 테이블을_생성한다(null);

    public static final OrderTable 빈문자이름_테이블 = 테이블을_생성한다("");

    public static final OrderTable 점유하고있는_테이블_1 = 테이블을_생성한다("1", 2, true);

    public static final OrderTable 점유하고있는_테이블_2 = 테이블을_생성한다("2", 3, true);

    public static final OrderTable 점유하지_않고_있는_테이블_1 = 테이블을_생성한다("1");

    public static final OrderTable 점유하지_않고_있는_테이블_2 = 테이블을_생성한다("2");

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
