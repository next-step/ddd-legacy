package kitchenpos;

import kitchenpos.model.*;

import java.math.BigDecimal;
import java.util.Collections;

public class Fixture {

    public static OrderTable defaultOrderTable() {
        OrderTable defaultOrderTable = new OrderTable();
        defaultOrderTable.setId(1L);
        defaultOrderTable.setTableGroupId(1L);
        defaultOrderTable.setNumberOfGuests(4);
        defaultOrderTable.setEmpty(false);
        return defaultOrderTable;
    }

    public static OrderTable emptyGroupIdOrderTable() {
        OrderTable emptyGroupIdOrderTable = new OrderTable();
        emptyGroupIdOrderTable.setId(2L);
        emptyGroupIdOrderTable.setTableGroupId(null);
        emptyGroupIdOrderTable.setNumberOfGuests(4);
        emptyGroupIdOrderTable.setEmpty(false);
        return emptyGroupIdOrderTable;
    }

    public static OrderTable emptyTrueOrderTable() {
        OrderTable emptyTrueOrderTable = new OrderTable();
        emptyTrueOrderTable.setId(3L);
        emptyTrueOrderTable.setTableGroupId(null);
        emptyTrueOrderTable.setNumberOfGuests(0);
        emptyTrueOrderTable.setEmpty(true);
        return emptyTrueOrderTable;
    }

    public static Product defaultProduct() {
        Product defaultProduct = new Product();
        defaultProduct.setName("맛있는 찌낑");
        defaultProduct.setPrice(BigDecimal.valueOf(3000));
        defaultProduct.setId(1L);
        return defaultProduct;
    }

    public static MenuGroup defaultMenuGroup() {
        MenuGroup defaultMenuGroup = new MenuGroup();
        defaultMenuGroup.setId(1L);
        defaultMenuGroup.setName("신메뉴");
        return defaultMenuGroup;
    }

    public static Order cookingOrder() {
        Order defaultOrder = new Order();
        defaultOrder.setId(1L);
        defaultOrder.setOrderTableId(1L);
        defaultOrder.setOrderStatus(OrderStatus.COOKING.toString());
        return defaultOrder;
    }

    public static Menu defaultMenu() {
        Menu defaultMenu = new Menu();
        defaultMenu.setId(1L);
        defaultMenu.setPrice(BigDecimal.valueOf(5000));
        defaultMenu.setMenuGroupId(1L);
        defaultMenu.setMenuProducts(Collections.singletonList(defaultMenuProduct()));
        return defaultMenu;
    }

    public static Menu emptyMenuGroupMenu() {
        Menu defaultMenu = new Menu();
        defaultMenu.setId(2L);
        defaultMenu.setName("넘모 힘들어요");
        defaultMenu.setPrice(BigDecimal.valueOf(5000));
        defaultMenu.setMenuGroupId(5L);
        defaultMenu.setMenuProducts(Collections.singletonList(defaultMenuProduct()));
        return defaultMenu;
    }

    private static MenuProduct defaultMenuProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setMenuId(1L);
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(2L);
        return menuProduct;
    }
}
