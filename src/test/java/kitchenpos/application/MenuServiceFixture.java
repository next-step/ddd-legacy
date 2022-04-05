package kitchenpos.application;

import static kitchenpos.application.ProductServiceFixture.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public final class MenuServiceFixture {

    private MenuServiceFixture() {

    }

    public static Menu menu() {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("후라이드 세트");
        menu.setPrice(BigDecimal.valueOf(18_000));
        menu.setMenuProducts(menuProducts());
        return menu;
    }

    public static List<Menu> menus() {
        List<Menu> menus = new ArrayList<>();
        menus.add(menu());
        return menus;
    }

    public static MenuProduct menuProduct() {
        Product product = product();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1L);
        return menuProduct;
    }

    public static List<MenuProduct> menuProducts() {
        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(menuProduct());
        return menuProducts;
    }

}
