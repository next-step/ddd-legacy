package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuGroupFixtures.createMenuGroup;
import static kitchenpos.fixture.ProductFixtures.createProduct;

public class MenuFixtures {

    public static Menu createMenu() {
        return createMenu(new BigDecimal("1000"), "메뉴", List.of(createMenuProduct()), createMenuGroup(), true);
    }

    public static Menu createMenu(BigDecimal price, String name, List<MenuProduct> menuProducts) {
        return createMenu(price, name, menuProducts, createMenuGroup(), true);
    }

    public static Menu createMenu(BigDecimal price, String name, List<MenuProduct> menuProducts, boolean displayed) {
        return createMenu(price, name, menuProducts, createMenuGroup(), displayed);
    }

    public static Menu createMenu(BigDecimal price, String name, List<MenuProduct> menuProducts, MenuGroup menuGroup, boolean displayed) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(price);
        menu.setName(name);
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(displayed);
        return menu;
    }

    public static MenuProduct createMenuProduct() {
        return createMenuProduct(createProduct(), 1L);
    }

    public static MenuProduct createMenuProduct(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
