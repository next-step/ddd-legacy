package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class TestFixture {
    public static MenuGroup TEST_MENU_GROUP() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("테스트 메뉴 그룹");
        menuGroup.setId(UUID.randomUUID());
        return menuGroup;
    }

    public static Menu TEST_MENU() {
        Menu menu = new Menu();
        menu.setName("테스트 메뉴");
        menu.setPrice(new BigDecimal(10_00));
        MenuGroup menuGroup = TEST_MENU_GROUP();
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(TEST_MENU_PRODUCT()));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setId(UUID.randomUUID());
        return menu;
    }

    public static MenuProduct TEST_MENU_PRODUCT() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(3);
        Product product = TEST_PRODUCT();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        return menuProduct;
    }

    public static Product TEST_PRODUCT() {
        Product product = new Product();
        product.setPrice(new BigDecimal(5_000));
        product.setName("핏자");
        product.setId(UUID.randomUUID());
        return product;
    }

    public static OrderTable TEST_ORDER_TABLE() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setOccupied(false);
        orderTable.setName("1번 테이블");
        orderTable.setNumberOfGuests(0);
        return orderTable;
    }
}
