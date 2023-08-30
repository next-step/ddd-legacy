package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuGroupFixture.TEST_MENU_GROUP;
import static kitchenpos.fixture.ProductFixture.CREATE_TEST_PRODUCT;

public class MenuFixture {

    public static Menu CREATE_TEST_MENU() {
        return CREATE_TEST_MENU(new BigDecimal(10_000), TEST_MENU_GROUP(), true, TEST_MENU_PRODUCT());
    }

    public static Menu CREATE_TEST_MENU(Product product) {
        return CREATE_TEST_MENU(new BigDecimal(10_000), TEST_MENU_GROUP(), true, TEST_MENU_PRODUCT(product));
    }

    public static Menu CREATE_TEST_MENU(MenuProduct menuProduct) {
        return CREATE_TEST_MENU(new BigDecimal(10_000), TEST_MENU_GROUP(), true, menuProduct);
    }

    public static Menu CREATE_TEST_MENU(boolean displayed) {
        return CREATE_TEST_MENU(new BigDecimal(10_000), TEST_MENU_GROUP(), displayed, TEST_MENU_PRODUCT());
    }
    public static Menu CREATE_TEST_MENU(BigDecimal price) {
        return CREATE_TEST_MENU(price, TEST_MENU_GROUP(), true, TEST_MENU_PRODUCT());
    }

    public static Menu CREATE_TEST_MENU(BigDecimal price, Product product) {
        return CREATE_TEST_MENU(price, TEST_MENU_GROUP(), true, TEST_MENU_PRODUCT(product));
    }

    public static Menu CREATE_TEST_MENU(BigDecimal price, MenuGroup menuGroup, boolean displayed, MenuProduct menuProduct) {
        Menu menu = new Menu();
        menu.setName("테스트 메뉴");
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(displayed);
        List<MenuProduct> menuProducts = menuProduct == null ? Collections.emptyList() : List.of(menuProduct);
        menu.setMenuProducts(menuProducts);
        menu.setId(UUID.randomUUID());
        return menu;
    }

    public static MenuProduct TEST_MENU_PRODUCT() {
        return TEST_MENU_PRODUCT(3, CREATE_TEST_PRODUCT());
    }

    public static MenuProduct TEST_MENU_PRODUCT(int quantity) {
        return TEST_MENU_PRODUCT(quantity, CREATE_TEST_PRODUCT());
    }

    public static MenuProduct TEST_MENU_PRODUCT(Product product) {
        return TEST_MENU_PRODUCT(3, product);
    }

    public static MenuProduct TEST_MENU_PRODUCT(int quantity, Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        return menuProduct;
    }

    public static BigDecimal MINIMUM_PRICE = new BigDecimal(1);
    public static BigDecimal MAX_PRICE = new BigDecimal(999_999_999);
}
