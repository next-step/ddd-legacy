package kitchenpos.constant;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public final class Fixtures {
    public static final Product PRODUCT;

    static {
        PRODUCT = new Product();
        PRODUCT.setName("SampleProduct");
        PRODUCT.setPrice(BigDecimal.valueOf(5000));
    }

    public static final MenuGroup MENU_GROUP;

    static {
        MENU_GROUP = new MenuGroup();
        MENU_GROUP.setName("SampleMenuGroup");
    }

    public static final MenuProduct MENU_PRODUCT;

    static {
        MENU_PRODUCT = new MenuProduct();
        MENU_PRODUCT.setProduct(PRODUCT);
        MENU_PRODUCT.setQuantity(1);
    }

    public static final Menu MENU;

    static {
        MENU = new Menu();
        MENU.setName("SampleMenu");
        MENU.setPrice(BigDecimal.valueOf(5000));
        MENU.setMenuGroup(MENU_GROUP);
        MENU.setDisplayed(true);
        MENU.setMenuProducts(List.of(MENU_PRODUCT));
    }
}
