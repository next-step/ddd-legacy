package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuGroupFixture.TEST_MENU_GROUP;
import static kitchenpos.fixture.ProductFixture.TEST_PRODUCT;

public class MenuFixture {

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

    public static Menu TEST_MENU_BY_PRODUCT(Product product) {
        Menu menu = new Menu();
        menu.setName("테스트 메뉴");
        menu.setPrice(new BigDecimal(10_00));
        MenuGroup menuGroup = TEST_MENU_GROUP();
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        MenuProduct menuProduct = TEST_MENU_PRODUCT();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menu.setMenuProducts(List.of(menuProduct));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setId(UUID.randomUUID());
        return menu;
    }

    public static BigDecimal MINIMUM_PRICE = new BigDecimal(1);
    public static BigDecimal MAX_PRICE = new BigDecimal(999_999_999);
}
