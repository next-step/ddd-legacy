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



    public static Menu createMenu(
            String name,
            BigDecimal price,
            MenuGroup menuGroup,
            boolean displayed,
            List<MenuProduct> menuProducts
    ) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu ofPrice(BigDecimal price) {
        return createMenu("메뉴", price, createMenuGroup(), false, List.of(createMenuProduct()));
    }

    public static MenuProduct createMenuProduct() {
        MenuProduct menuProduct = new MenuProduct();
        Product product = createProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(2);
        return menuProduct;
    }
    public static MenuProduct createMenuProduct(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
