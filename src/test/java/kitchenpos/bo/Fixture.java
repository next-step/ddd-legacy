package kitchenpos.bo;

import kitchenpos.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

public class Fixture {
    public static final Long MENU_GROUP_ID = 1L;
    public static final Long PRODUCT_ID = 1L;
    public static final Long TABLE_GROUP_ID = 1L;
    public static final Long ORDER_TABLE1_ID = 1L;
    public static final Long ORDER_TABLE2_ID = 2L;

    public static MenuProduct menuProduct() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setMenuId(PRODUCT_ID);
        menuProduct.setProductId(PRODUCT_ID);
        menuProduct.setQuantity(2);
        return menuProduct;
    }

    public static Product friedChicken() {
        final Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("후라이드");
        product.setPrice(BigDecimal.valueOf(16_000L));
        return product;
    }

    public static Menu twoFriedChickens() {
        final Menu menu = new Menu();
        menu.setId(PRODUCT_ID);
        menu.setName("후라이드2개");
        menu.setMenuGroupId(MENU_GROUP_ID);
        menu.setPrice(BigDecimal.valueOf(32_000L));
        menu.setMenuProducts(Collections.singletonList(menuProduct()));
        return menu;
    }

    public static OrderTable orderTable1() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(ORDER_TABLE1_ID);
        orderTable.setTableGroupId(null);
        orderTable.setNumberOfGuests(0);
        orderTable.setEmpty(false);
        return orderTable;
    }

    public static OrderTable orderTable2() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(ORDER_TABLE2_ID);
        orderTable.setTableGroupId(null);
        orderTable.setNumberOfGuests(0);
        orderTable.setEmpty(false);
        return orderTable;
    }

    public static OrderTable groupedTable1() {
        final OrderTable orderTable = orderTable1();
        orderTable.setTableGroupId(TABLE_GROUP_ID);
        return orderTable;
    }

    public static OrderTable groupedTable2() {
        final OrderTable orderTable = orderTable2();
        orderTable.setTableGroupId(TABLE_GROUP_ID);
        return orderTable;
    }

    public static TableGroup tableGroup() {
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(TABLE_GROUP_ID);
        tableGroup.setOrderTables(Arrays.asList(groupedTable1(), groupedTable2()));
        tableGroup.setCreatedDate(LocalDateTime.of(2020, 1, 1, 1, 0));
        return tableGroup;
    }

    public static OrderTable emptyTable1() {
        final OrderTable orderTable = orderTable1();
        orderTable.setEmpty(true);
        return orderTable;
    }
}
