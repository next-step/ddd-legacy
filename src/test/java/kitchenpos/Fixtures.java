package kitchenpos;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Fixtures {

    public static MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static Menu createMenu(BigDecimal price,
                                  UUID menuGroupId,
                                  MenuGroup menuGroup,
                                  List<MenuProduct> products,
                                  String name,
                                  boolean displayed) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(products);
        menu.setName(name);
        menu.setDisplayed(displayed);
        return menu;
    }

    public static MenuProduct createMenuProduct(Product product, Long seq, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setSeq(seq);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static Product createProduct(String name, Long price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

}
