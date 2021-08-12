package kitchenpos.application.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class MenuFixture {
    public static Menu generateMenu(MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        String name = menuProducts.stream()
                .map(MenuProduct::getProduct)
                .map(Product::getName)
                .reduce((name01, name02) -> name01 + " & " + name02)
                .get();
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(20000));
        menu.setDisplayed(true);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static List<MenuProduct> generateMenuProducts(Product... products) {
        List<MenuProduct> menuProducts = new LinkedList<>();
        for (Product product : products) {
            menuProducts.add(generateMenuProduct(product));
        }
        return menuProducts;
    }

    public static MenuProduct generateMenuProduct(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1L);
        return menuProduct;
    }

    public static MenuGroup generateMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("2가지 세트 메뉴");
        menuGroup.setId(UUID.randomUUID());
        return menuGroup;
    }

}
