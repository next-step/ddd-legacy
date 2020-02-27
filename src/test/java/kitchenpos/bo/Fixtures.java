package kitchenpos.bo;

import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeAll;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Geonguk Han
 * @since 2020-02-15
 */
public class Fixtures {

    private static final int QUANTITY_ONE = 1;
    private static final long PRODUCT_ID = 1L;
    private static final long PRODUCT_ID2 = 2L;
    private static final long MENU_ID = 1L;
    private static final long MENU_GROUP_ID = 1L;
    private static final long MENU_GROUP_ID2 = 2L;
    private static final long TABLE_GROUP_ID = 1L;
    private static final long ORDER_TABLE_ID = 1L;
    private static final long ORDER_TABLE_ID2 = 2L;
    private static final long ORDER_TABLE_ID3 = 3L;
    private static final long SEQ_ID = 1L;
    private static final long SEQ_ID2 = 2L;

    public static List<Product> products = Collections.emptyList();
    public static List<MenuGroup> menuGroups = Collections.emptyList();
    public static Menu menu;
    public static List<MenuProduct> menuProducts = Collections.emptyList();
    public static List<OrderTable> orderTables = Collections.emptyList();
    public static OrderTable orderTable;

    @BeforeAll
    public static void setUp() {
        makeProduct();
        makeMenuGroup();
        makeMenu();
        makeMenuProduct();
        makeOrderTable();
    }

    private static void makeProduct() {
        final Product product1 = new Product();
        product1.setId(PRODUCT_ID);
        product1.setName("짜장면");
        product1.setPrice(BigDecimal.valueOf(6000));

        final Product product2 = new Product();
        product2.setId(PRODUCT_ID2);
        product2.setName("짬봉");
        product2.setPrice(BigDecimal.valueOf(7000));

        products = Arrays.asList(product1, product2);
    }

    private static void makeMenuGroup() {
        final MenuGroup menuGroup1 = new MenuGroup();
        menuGroup1.setId(MENU_GROUP_ID);
        menuGroup1.setName("면 요리 세트");

        final MenuGroup menuGroup2 = new MenuGroup();
        menuGroup2.setId(MENU_GROUP_ID2);
        menuGroup2.setName("밥 요리 세트");

        menuGroups = Arrays.asList(menuGroup1, menuGroup2);
    }

    private static void makeMenu() {
        final Menu newMenu = new Menu();
        newMenu.setId(MENU_ID);
        newMenu.setName("메뉴");
        newMenu.setPrice(products.get(0).getPrice().add(products.get(1).getPrice()));
        newMenu.setMenuGroupId(menuGroups.get(0).getId());

        menu = newMenu;
    }

    private static void makeMenuProduct() {
        final MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setSeq(SEQ_ID);
        menuProduct1.setMenuId(MENU_ID);
        menuProduct1.setProductId(PRODUCT_ID);
        menuProduct1.setQuantity(QUANTITY_ONE);

        final MenuProduct menuProduct2 = new MenuProduct();
        menuProduct2.setSeq(SEQ_ID2);
        menuProduct2.setMenuId(MENU_ID);
        menuProduct2.setProductId(PRODUCT_ID2);
        menuProduct2.setQuantity(QUANTITY_ONE);

        menuProducts = Arrays.asList(menuProduct1, menuProduct2);
    }

    private static void makeOrderTable() {
        final OrderTable newOrderTable1 = new OrderTable();
        newOrderTable1.setId(ORDER_TABLE_ID);
        newOrderTable1.setTableGroupId(TABLE_GROUP_ID);
        newOrderTable1.setNumberOfGuests(2);
        newOrderTable1.setEmpty(false);

        orderTable = newOrderTable1;

        final OrderTable newOrderTable2 = new OrderTable();
        newOrderTable2.setId(ORDER_TABLE_ID2);
        newOrderTable2.setTableGroupId(TABLE_GROUP_ID);
        newOrderTable2.setNumberOfGuests(4);
        newOrderTable2.setEmpty(false);

        final OrderTable newOrderTable3 = new OrderTable();
        newOrderTable3.setId(ORDER_TABLE_ID3);
        newOrderTable3.setTableGroupId(TABLE_GROUP_ID);
        newOrderTable3.setNumberOfGuests(4);
        newOrderTable3.setEmpty(false);

        orderTables = Arrays.asList(newOrderTable1, newOrderTable2, newOrderTable3);
    }
}
