package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public class TestFixture {
    public static MenuGroup TEST_MENU_GROUP() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("테스트 메뉴 그룹");
        return menuGroup;
    }

    public static Menu TEST_MENU() {
        Menu menu = new Menu();
        menu.setName("테스트 메뉴");
        menu.setPrice(new BigDecimal(10_00));
        menu.setMenuGroup(TEST_MENU_GROUP());
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(TEST_MENU_PRODUCT()));
        return menu;
    }

    public static MenuProduct TEST_MENU_PRODUCT() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(1);
        return menuProduct;
    }

    public static Product TEST_PRODUCT() {
        Product product = new Product();
        product.setPrice(new BigDecimal(5_000));
        product.setName("핏자");
        return product;
    }
}
