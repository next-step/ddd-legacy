package kitchenpos.application.menu;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuTestFixture {

    public static MenuGroup aMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("레허순살반반");
        return menuGroup;
    }


    public static Product aProduct(final String name, final Long price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static MenuProduct aMenuProduct(
            final Long seq,
            final Product product,
            final long quantity
    ) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(seq);
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static Menu aMenu(String name, Long price, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(true);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu aMenuWithOutMenuGroup(String name, Long price, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu aMenuJustPrice(String name, Long price) {
        BigDecimal decimalPrice = price == null ? null : BigDecimal.valueOf(price);
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(decimalPrice);
        return menu;
    }


}
