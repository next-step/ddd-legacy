package kitchenpos;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class Fixtures {

    public static Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);

        return product;
    }

    public static MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }
}
