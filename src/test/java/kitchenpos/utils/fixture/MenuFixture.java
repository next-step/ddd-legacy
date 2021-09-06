package kitchenpos.utils.fixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.List;

import static java.util.UUID.randomUUID;

public class MenuFixture {

    public static MenuProduct 메뉴상품(Product product) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1L);
        return menuProduct;
    }

    public static Menu 메뉴(MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        final Menu menu = 기본메뉴();
        menu.setName("메뉴");
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu 기본메뉴() {
        final Menu menu = new Menu();
        menu.setId(randomUUID());
        menu.setName("메뉴");
        menu.setPrice(BigDecimal.valueOf(10_000L));
        menu.setDisplayed(true);
        return menu;
    }
}
