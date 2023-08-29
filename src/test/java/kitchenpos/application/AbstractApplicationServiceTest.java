package kitchenpos.application;


import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;


public abstract class AbstractApplicationServiceTest {

    protected Product createProductRequest(final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setName(name);
        product.setPrice(price);

        return product;
    }

    protected Menu createMenuRequest(final String name, final BigDecimal price,
        final List<MenuProduct> menuProducts, final MenuGroup menuGroup, final boolean display) {

        final Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(display);

        return menu;
    }

    protected MenuGroup createMenuGroupRequest(final String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);

        return menuGroup;
    }

    protected MenuProduct createMenuProductRequest(final Product product, final long quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);

        return menuProduct;
    }
}
