package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Fixtures {

    static MenuGroup aMenuGroup() {
        MenuGroup menuGroup = new MenuGroup("한마리치킨");
        menuGroup.setId(UUID.randomUUID());
        return menuGroup;
    }

    static Product aProduct(int price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("후라이드 치킨");
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    static MenuProduct aMenuProduct(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    static Menu aMenu(String name, int price, MenuProduct... menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuProducts(List.of(menuProducts));
        menu.setDisplayed(true);
        menu.setMenuGroup(aMenuGroup());
        menu.setMenuGroupId(aMenuGroup().getId());
        return menu;
    }
}
