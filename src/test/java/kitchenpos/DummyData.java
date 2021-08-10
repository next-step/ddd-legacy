package kitchenpos;

import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DummyData {

    protected List<MenuGroup> menuGroups = new ArrayList<>();
    protected List<Product> products = new ArrayList<>();
    protected List<Menu> menus = new ArrayList<>();
    protected List<MenuProduct> menuProducts = new ArrayList<>();
    protected List<OrderTable> orderTables = new ArrayList<>();

    protected static final UUID FIRST_ID = UUID.randomUUID();
    protected static final UUID SECOND_ID = UUID.randomUUID();

    protected static final Boolean MENU_HIDE = false;
    protected static final Boolean MENU_SHOW = true;

    protected static final Boolean TABLE_CLEAR = true;
    protected static final Boolean TABLE_SIT = false;
    protected static final int TABLE_DEFAULT_NUMBER_OF_GUEST = 0;

    @BeforeEach
    void setUp() {
        setMenuGroups();
        setProducts();
        setMenuProducts();
        setMenus();
        setOrderTables();
    }

    private void setMenuGroups() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(FIRST_ID);
        menuGroup.setName("추천메뉴");

        MenuGroup menuGroup2 = new MenuGroup();
        menuGroup.setId(SECOND_ID);
        menuGroup.setName("중식");

        menuGroups.add(menuGroup);
        menuGroups.add(menuGroup2);
    }

    private void setProducts() {
        Product product = new Product();
        product.setId(FIRST_ID);
        product.setPrice(ofPrice(1000));
        product.setName("후라이드");

        Product product2 = new Product();
        product.setId(SECOND_ID);
        product.setPrice(ofPrice(1100));
        product.setName("양념");

        products.add(product);
        products.add(product2);
    }

    private void setMenuProducts() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setProduct(products.get(0));
        menuProduct.setQuantity(1L);

        MenuProduct menuProduct2 = new MenuProduct();
        menuProduct2.setSeq(2L);
        menuProduct2.setProduct(products.get(1));
        menuProduct2.setQuantity(1L);

        menuProducts.add(menuProduct);
        menuProducts.add(menuProduct2);
    };

    private void setMenus() {
        Menu menu = new Menu();
        menu.setId(FIRST_ID);
        menu.setMenuGroup(menuGroups.get(0));
        menu.setName("점심 한정");
        menu.setPrice(ofPrice(900));
        menu.setDisplayed(MENU_SHOW);
        menu.setMenuProducts(Arrays.asList(menuProducts.get(0)));

        Menu menu2 = new Menu();
        menu2.setId(SECOND_ID);
        menu2.setMenuGroup(menuGroups.get(1));
        menu2.setName("저녁 한정");
        menu2.setPrice(ofPrice(1000));
        menu2.setDisplayed(MENU_SHOW);
        menu2.setMenuProducts(Arrays.asList(menuProducts.get(1)));

        menus.add(menu);
        menus.add(menu2);
    }

    private void setOrderTables() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName("1번");
        orderTable.setId(FIRST_ID);
        orderTable.setEmpty(TABLE_CLEAR);
        orderTable.setNumberOfGuests(TABLE_DEFAULT_NUMBER_OF_GUEST);

        OrderTable orderTable2 = new OrderTable();
        orderTable2.setName("2번");
        orderTable2.setId(SECOND_ID);
        orderTable2.setEmpty(TABLE_CLEAR);
        orderTable2.setNumberOfGuests(TABLE_DEFAULT_NUMBER_OF_GUEST);

        orderTables.add(orderTable);
        orderTables.add(orderTable2);
    }

    protected BigDecimal ofPrice(int price) {
        return BigDecimal.valueOf(price);
    }
}
