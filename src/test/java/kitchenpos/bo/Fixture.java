package kitchenpos.bo;

import kitchenpos.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Fixture {
    static MenuGroup 야식() {
        return MenuGroupBuilder.aMenuGroup()
                .withId(1L)
                .withName("야식")
                .build();
    }

    static Menu 치맥셋트() {
        return MenuBuilder.aMenu()
                .withMenuGroupId(1L)
                .withId(1L)
                .withName("치맥셋트")
                .withPrice(BigDecimal.valueOf(22000))
                .withMenuProducts(Arrays.asList(핫치킨(), 시원한생맥주()))
                .build();
    }

    static MenuProduct 핫치킨() {
        return MenuProductBuilder
                .aMenuProduct()
                .withMenuId(1L)
                .withProductId(1L)
                .withQuantity(1L)
                .withSeq(1L)
                .build();
    }

    static MenuProduct 시원한생맥주() {
        return MenuProductBuilder
                .aMenuProduct()
                .withMenuId(1L)
                .withProductId(2L)
                .withQuantity(2L)
                .withSeq(2L)
                .build();
    }

    static Product 치킨(){
        return ProductBuilder
                .aProduct()
                .withId(1L)
                .withName("치킨")
                .withPrice(BigDecimal.valueOf(17000))
                .build();
    }


    static Product 맥주(){
        return ProductBuilder
                .aProduct()
                .withId(2L)
                .withName("맥주")
                .withPrice(BigDecimal.valueOf(3000))
                .build();
    }

    static Order 일번테이블주문(){
        return OrderBuilder
                .anOrder()
                .withOrderedTime(LocalDateTime.now())
                .withOrderLineItems(Arrays.asList(주문번호1_치맥주문()))
                .withOrderTableId(1L)
                .build();
    }

    static OrderLineItem 주문번호1_치맥주문(){
        return OrderLineItemBuilder
                .anOrderLineItem()
                .withSeq(1L)
                .withMenuId(1L)
                .withOrderId(1L)
                .withQuantity(1L)
                .build();
    }

    static OrderTable 만석인_일번테이블(){
        return OrderTableBuilder
                .anOrderTable()
                .withId(1L)
                .withTableGroupId(1L)
                .withNumberOfGuests(5)
                .withEmpty(false)
                .build();
    }

    static OrderTable 만석인_이번테이블(){
        return OrderTableBuilder
                .anOrderTable()
                .withId(2L)
                .withTableGroupId(1L)
                .withNumberOfGuests(4)
                .withEmpty(false)
                .build();
    }

    static OrderTable 비어있는_삼번테이블(){
        return OrderTableBuilder
                .anOrderTable()
                .withId(3L)
                .withNumberOfGuests(4)
                .withEmpty(true)
                .build();
    }
    static OrderTable 비어있는_사번테이블(){
        return OrderTableBuilder
                .anOrderTable()
                .withId(4L)
                .withNumberOfGuests(4)
                .withEmpty(true)
                .build();
    }

    static TableGroup 단체테이블1(){
        return TableGroupBuilder
                .aTableGroup()
                .withId(1L)
                .withOrderTables(Arrays.asList(만석인_일번테이블(), 만석인_이번테이블()))
                .withCreatedDate(LocalDateTime.now())
                .build();
    }

    static TableGroup 단체테이블2(){
        return TableGroupBuilder
                .aTableGroup()
                .withId(2L)
                .withOrderTables(Arrays.asList(비어있는_삼번테이블(), 비어있는_사번테이블()))
                .withCreatedDate(LocalDateTime.now())
                .build();
    }

}
