package kitchenpos.constant;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public final class Fixtures {
    public static final Product PRODUCT = new Product();
    public static final MenuGroup MENU_GROUP = new MenuGroup();
    public static final MenuProduct MENU_PRODUCT = new MenuProduct();
    public static final Menu MENU = new Menu();

    static {
        initialize();
    }

    public static void initialize() {
        PRODUCT.setId(UUID.randomUUID());
        PRODUCT.setName("SampleProduct");
        PRODUCT.setPrice(BigDecimal.valueOf(5000));

        MENU_GROUP.setId(UUID.randomUUID());
        MENU_GROUP.setName("SampleMenuGroup");

        MENU_PRODUCT.setSeq(1L);
        MENU_PRODUCT.setProduct(PRODUCT);
        MENU_PRODUCT.setQuantity(1);

        MENU.setId(UUID.randomUUID());
        MENU.setName("SampleMenu");
        MENU.setPrice(BigDecimal.valueOf(5000));
        MENU.setMenuGroup(MENU_GROUP);
        MENU.setDisplayed(true);
        MENU.setMenuProducts(List.of(MENU_PRODUCT));
    }
}
