package kitchenpos.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public final class Fixture {

    public static final BigDecimal PRICES_FOR_ALL_PRODUCTS_ON_THE_MENU = BigDecimal.valueOf(16_000);

    private Fixture() {
    }

    public static Product createProduct() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("후라이드");
        product.setPrice(BigDecimal.valueOf(16_000));
        return product;
    }

    public static MenuProduct createMenuProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(createProduct());
        menuProduct.setSeq(0L);
        menuProduct.setQuantity(1);
        return menuProduct;
    }

    public static Menu createMenu() {
        Menu menu = new Menu();
        menu.setName("후라이드 1개");
        menu.setDisplayed(Boolean.TRUE);
        menu.setPrice(BigDecimal.valueOf(15_000));

        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(createMenuProduct());
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static MenuGroup createMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("한마리 메뉴");
        return menuGroup;
    }
}
