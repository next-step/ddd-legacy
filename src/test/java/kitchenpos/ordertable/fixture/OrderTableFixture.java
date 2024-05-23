package kitchenpos.ordertable.fixture;

import kitchenpos.domain.OrderTable;

import static kitchenpos.support.util.random.RandomNumberOfGuestsUtil.랜덤한_1명이상_6명이하_인원을_생성한다;

public class OrderTableFixture {

    public static final OrderTable A_테이블 = 테이블을_생성한다("A");

    public static final OrderTable B_테이블 = 테이블을_생성한다("B");

    public static final OrderTable 이름미존재_테이블 = 테이블을_생성한다(null);

    public static final OrderTable 빈문자이름_테이블 = 테이블을_생성한다("");

    public static final OrderTable 비어있는_테이블_C = 테이블을_생성한다("C");

    public static final OrderTable 비어있는_테이블_D = 테이블을_생성한다("D");

    public static final OrderTable 점유하고있는_테이블 = 테이블을_생성한다("D", 랜덤한_1명이상_6명이하_인원을_생성한다(), true);

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
