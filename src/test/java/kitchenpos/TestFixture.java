package kitchenpos;

import kitchenpos.builder.*;
import kitchenpos.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

public class TestFixture {
    public static final long LONG_ONE = 1L;
    public static final long LONG_TWO = 2L;

    public static final int INT_ZERO = 0;
    public static final int INT_ONE = 1;
    public static final int INT_TWO = 2;
    public static final int INT_THREE = 3;

    public static MenuProduct generateMenuProductOne() {
        return new MenuProductBuilder()
                .setProductId(LONG_ONE)
                .setMenuId(LONG_ONE)
                .setSeq(LONG_ONE)
                .setQuantity(INT_TWO)
                .build()
                ;
    }

    public static MenuProduct generateMenuProductTwo() {
        return new MenuProductBuilder()
                .setProductId(LONG_TWO)
                .setMenuId(LONG_ONE)
                .setSeq(LONG_ONE)
                .setQuantity(INT_TWO)
                .build()
                ;
    }

    public static Menu generateMenuOne() {
        return new MenuBuilder()
                .setMenuGroupId(LONG_ONE)
                .setId(LONG_ONE)
                .setPrice(BigDecimal.TEN)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(generateMenuProductOne()))
                .build()
                ;
    }

    public static Menu generateMenuTwo() {
        return new MenuBuilder()
                .setMenuGroupId(LONG_TWO)
                .setId(LONG_TWO)
                .setPrice(BigDecimal.TEN)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(generateMenuProductTwo()))
                .build()
                ;
    }

    public static Menu generateMenuHasTwoProduct() {
        return new MenuBuilder()
                .setMenuGroupId(LONG_ONE)
                .setId(LONG_ONE)
                .setPrice(BigDecimal.TEN)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(generateMenuProductOne(), generateMenuProductTwo()))
                .build()
                ;
    }

    public static OrderLineItem generateOrderLineItemOne() {
        return new OrderLineItemBuilder()
                .setOrderId(LONG_ONE)
                .setQuantity(INT_THREE)
                .setMenuId(LONG_ONE)
                .setSeq(LONG_ONE)
                .build()
                ;
    }

    public static OrderLineItem generateOrderLineItemTwo() {
        return new OrderLineItemBuilder()
                .setOrderId(LONG_TWO)
                .setQuantity(INT_ONE)
                .setMenuId(LONG_TWO)
                .setSeq(LONG_TWO)
                .build()
                ;
    }

    public static Order generateOrderOne() {
        return new OrderBuilder()
                .setId(LONG_ONE)
                .setOrderLineItems(Arrays.asList(generateOrderLineItemOne(), generateOrderLineItemTwo()))
                .setOrderTableId(LONG_ONE)
                .build()
                ;
    }

    public static Order generateOrderCooking() {
        return new OrderBuilder()
                .setId(LONG_ONE)
                .setOrderStatus(OrderStatus.COOKING.name())
                .setOrderLineItems(Arrays.asList(generateOrderLineItemOne(), generateOrderLineItemTwo()))
                .setOrderTableId(LONG_ONE)
                .build()
                ;
    }

    public static Order generateOrderCompletion() {
        return new OrderBuilder()
                .setId(LONG_ONE)
                .setOrderStatus(OrderStatus.COMPLETION.name())
                .setOrderLineItems(Arrays.asList(generateOrderLineItemOne(), generateOrderLineItemTwo()))
                .setOrderTableId(LONG_ONE)
                .build()
                ;
    }

    public static OrderTable generateOrderTableNotEmpty() {
        return new OrderTableBuilder()
                .setEmpty(false)
                .setNumberOfGuests(INT_THREE)
                .setId(LONG_ONE)
                .build()
                ;
    }

    public static OrderTable generateOrderTableOne() {
        return new OrderTableBuilder()
                .setEmpty(true)
                .setNumberOfGuests(INT_ZERO)
                .build()
                ;
    }

    public static OrderTable generateOrderTableEmptyOne() {
        return new OrderTableBuilder()
                .setEmpty(true)
                .setNumberOfGuests(INT_ZERO)
                .setId(LONG_ONE)
                .build()
                ;
    }

    public static OrderTable generateOrderTableEmptyTWo() {
        return new OrderTableBuilder()
                .setEmpty(true)
                .setNumberOfGuests(INT_ZERO)
                .setId(LONG_TWO)
                .build()
                ;
    }

    public static TableGroup generateTableGroupOne() {
        return new TableGroupBuilder()
                .setId(LONG_ONE)
                .setOrderTables(Arrays.asList(generateOrderTableEmptyOne(), generateOrderTableEmptyTWo()))
                .setCreatedDate(LocalDateTime.now())
                .build()
                ;
    }

    public static TableGroup generateTableGroupHasOneOrderTable() {
        return new TableGroupBuilder()
                .setId(LONG_ONE)
                .setOrderTables(Arrays.asList(generateOrderTableEmptyOne()))
                .setCreatedDate(LocalDateTime.now())
                .build()
                ;
    }
}
