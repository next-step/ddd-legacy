package kitchenpos;

import kitchenpos.builder.*;
import kitchenpos.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

public class TestFixture {
    private static final long MENU_PRODUCT_ID_ONE = 1L;
    private static final long MENU_PRODUCT_SEQ_ONE = 1L;

    private static final long MENU_ID_ONE = 1L;
    private static final long MENU_ID_TWO = 2L;

    private static final long MENU_GROUP_ID_ONE = 1L;
    private static final long MENU_GROUP_ID_TWO = 2L;

    private static final long ORDER_ID_ONE = 1L;
    private static final long ORDER_ID_TWO = 2L;

    private static final long ORDER_TABLE_ID_ONE = 1L;
    private static final long ORDER_TABLE_ID_TWO = 2L;

    private static final long ORDER_LINE_SEQ_ONE = 1L;
    private static final long ORDER_LINE_SEQ_TWO = 2L;

    private static final long TABLE_GROUP_ID_ONE = 1L;

    private static final int MENU_PRODUCT_QUANTITY_TWO = 2;

    private static final int ORDER_LINE_QUANTITY_ONE = 1;
    private static final int ORDER_LINE_QUANTITY_THREE = 3;

    private static final int ORDER_TABLE_GUEST_ZERO = 0;
    private static final int ORDER_TABLE_GUEST_THREE = 3;


    private static final String MENU_NAME_ONE = "간장치킨";

    public static MenuProduct generateMenuProductOne() {
        return new MenuProductBuilder()
                .setProductId(MENU_PRODUCT_ID_ONE)
                .setMenuId(MENU_ID_ONE)
                .setSeq(MENU_PRODUCT_SEQ_ONE)
                .setQuantity(MENU_PRODUCT_QUANTITY_TWO)
                .build()
                ;
    }

    public static MenuProduct generateMenuProductTwo() {
        return new MenuProductBuilder()
                .setProductId(MENU_PRODUCT_ID_ONE)
                .setMenuId(MENU_ID_ONE)
                .setSeq(MENU_PRODUCT_SEQ_ONE)
                .setQuantity(MENU_PRODUCT_QUANTITY_TWO)
                .build()
                ;
    }

    public static Menu generateMenuOne() {
        return new MenuBuilder()
                .setMenuGroupId(MENU_GROUP_ID_ONE)
                .setId(MENU_ID_ONE)
                .setPrice(BigDecimal.TEN)
                .setName(MENU_NAME_ONE)
                .setMenuProducts(Arrays.asList(generateMenuProductOne()))
                .build()
                ;
    }

    public static Menu generateMenuTwo() {
        return new MenuBuilder()
                .setMenuGroupId(MENU_GROUP_ID_TWO)
                .setId(MENU_ID_TWO)
                .setPrice(BigDecimal.TEN)
                .setName(MENU_NAME_ONE)
                .setMenuProducts(Arrays.asList(generateMenuProductTwo()))
                .build()
                ;
    }

    public static Menu generateMenuHasTwoProduct() {
        return new MenuBuilder()
                .setMenuGroupId(MENU_GROUP_ID_ONE)
                .setId(MENU_ID_ONE)
                .setPrice(BigDecimal.TEN)
                .setName(MENU_NAME_ONE)
                .setMenuProducts(Arrays.asList(generateMenuProductOne(), generateMenuProductTwo()))
                .build()
                ;
    }

    public static OrderLineItem generateOrderLineItemOne() {
        return new OrderLineItemBuilder()
                .setOrderId(ORDER_ID_ONE)
                .setQuantity(ORDER_LINE_QUANTITY_THREE)
                .setMenuId(MENU_ID_ONE)
                .setSeq(ORDER_LINE_SEQ_ONE)
                .build()
                ;
    }

    public static OrderLineItem generateOrderLineItemTwo() {
        return new OrderLineItemBuilder()
                .setOrderId(ORDER_ID_TWO)
                .setQuantity(ORDER_LINE_QUANTITY_ONE)
                .setMenuId(MENU_ID_TWO)
                .setSeq(ORDER_LINE_SEQ_TWO)
                .build()
                ;
    }

    public static Order generateOrderOne() {
        return new OrderBuilder()
                .setId(ORDER_ID_ONE)
                .setOrderLineItems(Arrays.asList(generateOrderLineItemOne(), generateOrderLineItemTwo()))
                .setOrderTableId(ORDER_TABLE_ID_ONE)
                .build()
                ;
    }

    public static Order generateOrderCooking() {
        return new OrderBuilder()
                .setId(ORDER_ID_ONE)
                .setOrderStatus(OrderStatus.COOKING.name())
                .setOrderLineItems(Arrays.asList(generateOrderLineItemOne(), generateOrderLineItemTwo()))
                .setOrderTableId(ORDER_TABLE_ID_ONE)
                .build()
                ;
    }

    public static Order generateOrderCompletion() {
        return new OrderBuilder()
                .setId(ORDER_ID_ONE)
                .setOrderStatus(OrderStatus.COMPLETION.name())
                .setOrderLineItems(Arrays.asList(generateOrderLineItemOne(), generateOrderLineItemTwo()))
                .setOrderTableId(ORDER_TABLE_ID_ONE)
                .build()
                ;
    }

    public static OrderTable generateOrderTableNotEmpty() {
        return new OrderTableBuilder()
                .setEmpty(false)
                .setNumberOfGuests(ORDER_TABLE_GUEST_THREE)
                .setId(ORDER_ID_ONE)
                .build()
                ;
    }

    public static OrderTable generateOrderTableOne() {
        return new OrderTableBuilder()
                .setEmpty(true)
                .setNumberOfGuests(ORDER_TABLE_GUEST_ZERO)
                .build()
                ;
    }

    public static OrderTable generateOrderTableEmptyOne() {
        return new OrderTableBuilder()
                .setEmpty(true)
                .setNumberOfGuests(ORDER_TABLE_GUEST_ZERO)
                .setId(ORDER_ID_ONE)
                .build()
                ;
    }

    public static OrderTable generateOrderTableEmptyTWo() {
        return new OrderTableBuilder()
                .setEmpty(true)
                .setNumberOfGuests(ORDER_TABLE_GUEST_ZERO)
                .setId(ORDER_TABLE_ID_TWO)
                .build()
                ;
    }

    public static TableGroup generateTableGroupOne() {
        return new TableGroupBuilder()
                .setId(TABLE_GROUP_ID_ONE)
                .setOrderTables(Arrays.asList(generateOrderTableEmptyOne(), generateOrderTableEmptyTWo()))
                .setCreatedDate(LocalDateTime.now())
                .build()
                ;
    }

    public static TableGroup generateTableGroupHasOneOrderTable() {
        return new TableGroupBuilder()
                .setId(TABLE_GROUP_ID_ONE)
                .setOrderTables(Arrays.asList(generateOrderTableEmptyOne()))
                .setCreatedDate(LocalDateTime.now())
                .build()
                ;
    }
}
