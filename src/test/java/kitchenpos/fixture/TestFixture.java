package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class TestFixture {

    public static Product makeTestProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static MenuProduct makeTestMenuProduct(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1);
        menuProduct.setSeq(1L);
        menuProduct.setProduct(product);
        return menuProduct;
    }

    public static Menu makeTestMenu(String name, BigDecimal price, MenuGroup menuGroup, MenuProduct menuProduct) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(List.of(menuProduct));
        return menu;
    }

    public static MenuGroup makeTestMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }
}
