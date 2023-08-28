package kitchenpos.objectmother;

import kitchenpos.domain.OrderTable;

public class OrderTableMaker {

    public static final OrderTable 테이블_이름없음 = make();
    public static final OrderTable 테이블_1 = make("테이블1");
    public static final OrderTable 테이블_2 = make("테이블2");
    public static final OrderTable 테이블_고객_음수 = make("테이블3", -4);
    public static final OrderTable 테이블_고객_4명 = make("테이블4", 4);

    public static OrderTable make() {
        return new OrderTable();
    }

    public static OrderTable make(String name) {
        return new OrderTable(name);
    }

    public static OrderTable make(String name, int numberOfGuests) {
        return new OrderTable(name, numberOfGuests, true);
    }

}
