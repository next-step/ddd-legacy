package kitchenpos.menu.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    public static Menu 메뉴_생성_요청(String name, int price, boolean displayed, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(new BigDecimal(price));
        menu.setDisplayed(displayed);
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static MenuProduct 메뉴_상품_생성_요청(long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static Menu 메뉴(final String name, final int price, boolean displayed, final MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(new BigDecimal(price));
        menu.setDisplayed(displayed);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static MenuProduct 메뉴_상품(final long seq, final Long quantity, final Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        menuProduct.setQuantity(quantity);
        menuProduct.setProduct(product);
        menuProduct.setSeq(seq);
        return menuProduct;
    }

    public static Product 상품(final String name, final int price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(new BigDecimal(price));
        return product;
    }

    public static MenuGroup 메뉴_그룹(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static Menu 메뉴_가격_변경_요청(final int price) {
        Menu menu = new Menu();
        menu.setPrice(new BigDecimal(price));
        return menu;
    }
}
