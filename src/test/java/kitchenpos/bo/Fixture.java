package kitchenpos.bo;

import kitchenpos.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Fixture {
    private static final long MENU_GROUP_ID_야식 = 1L;
    private static final long MENU_ID_치맥셋트 = 1L;
    private static final long PRODUCT_ID_치킨 = 1L;
    private static final long PRODUCT_ID_맥주 = 2L;
    private static final long MENU_PRODUCT_SEQ_핫치킨 = 1L;
    private static final long MENU_PRODUCT_SEQ_시원한생맥주 = 2L;
    private static final long ORDER_TABLE_ID_일번테이블 = 1L;
    private static final long ORDER_TABLE_ID_이번테이블 = 2L;
    private static final long ORDER_TABLE_ID_삼번테이블 = 3L;
    private static final long ORDER_TABLE_ID_사번테이블 = 4L;
    private static final long TABLE_GROUP_ID_단체1 = 1L;
    private static final long ORDER_ID_일번테이블주문 = 1L;
    private static final long ORDER_LINE_SEQ_주문번호1 = 1L;

    static MenuGroup 야식() {
        return MenuGroupBuilder.aMenuGroup()
                .withId(MENU_GROUP_ID_야식)
                .withName("야식")
                .build();
    }

    static Menu 치맥셋트() {
        return MenuBuilder.aMenu()
                .withMenuGroupId(MENU_GROUP_ID_야식)
                .withId(MENU_ID_치맥셋트)
                .withName("치맥셋트")
                .withPrice(BigDecimal.valueOf(22000))
                .withMenuProducts(Arrays.asList(핫치킨(), 시원한생맥주()))
                .build();
    }

    static MenuProduct 핫치킨() {
        return MenuProductBuilder
                .aMenuProduct()
                .withMenuId(MENU_ID_치맥셋트)
                .withProductId(PRODUCT_ID_치킨)
                .withQuantity(1L)
                .withSeq(MENU_PRODUCT_SEQ_핫치킨)
                .build();
    }

    static MenuProduct 시원한생맥주() {
        return MenuProductBuilder
                .aMenuProduct()
                .withMenuId(MENU_ID_치맥셋트)
                .withProductId(PRODUCT_ID_맥주)
                .withQuantity(2L)
                .withSeq(MENU_PRODUCT_SEQ_시원한생맥주)
                .build();
    }

    static Product 치킨() {
        return ProductBuilder
                .aProduct()
                .withId(PRODUCT_ID_치킨)
                .withName("치킨")
                .withPrice(BigDecimal.valueOf(17000))
                .build();
    }

    static Product 맥주() {
        return ProductBuilder
                .aProduct()
                .withId(PRODUCT_ID_맥주)
                .withName("맥주")
                .withPrice(BigDecimal.valueOf(3000))
                .build();
    }

    static Order 일번테이블주문() {
        return OrderBuilder
                .anOrder()
                .withOrderedTime(LocalDateTime.now())
                .withOrderLineItems(Arrays.asList(주문번호1_치맥주문()))
                .withOrderTableId(ORDER_TABLE_ID_일번테이블)
                .withId(ORDER_ID_일번테이블주문)
                .build();
    }

    static OrderLineItem 주문번호1_치맥주문() {
        return OrderLineItemBuilder
                .anOrderLineItem()
                .withSeq(ORDER_LINE_SEQ_주문번호1)
                .withMenuId(MENU_ID_치맥셋트)
                .withOrderId(ORDER_ID_일번테이블주문)
                .withQuantity(1L)
                .build();
    }

    static OrderTable 만석인_일번테이블() {
        return OrderTableBuilder
                .anOrderTable()
                .withId(ORDER_TABLE_ID_일번테이블)
                .withTableGroupId(TABLE_GROUP_ID_단체1)
                .withNumberOfGuests(5)
                .withEmpty(false)
                .build();
    }

    static OrderTable 만석인_이번테이블() {
        return OrderTableBuilder
                .anOrderTable()
                .withId(ORDER_TABLE_ID_이번테이블)
                .withTableGroupId(TABLE_GROUP_ID_단체1)
                .withNumberOfGuests(4)
                .withEmpty(false)
                .build();
    }

    static OrderTable 비어있는_삼번테이블() {
        return OrderTableBuilder
                .anOrderTable()
                .withId(ORDER_TABLE_ID_삼번테이블)
                .withNumberOfGuests(4)
                .withEmpty(true)
                .build();
    }

    static OrderTable 비어있는_사번테이블() {
        return OrderTableBuilder
                .anOrderTable()
                .withId(ORDER_TABLE_ID_사번테이블)
                .withNumberOfGuests(4)
                .withEmpty(true)
                .build();
    }

    static TableGroup 단체테이블1() {
        return TableGroupBuilder
                .aTableGroup()
                .withId(TABLE_GROUP_ID_단체1)
                .withOrderTables(Arrays.asList(만석인_일번테이블(), 만석인_이번테이블()))
                .withCreatedDate(LocalDateTime.now())
                .build();
    }
}
