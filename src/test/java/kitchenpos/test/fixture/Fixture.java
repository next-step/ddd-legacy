package kitchenpos.test.fixture;

import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public final class Fixture {

    public static final Product PRODUCT;
    public static final MenuProduct MENU_PRODUCT;
    public static final Menu MENU;

    static {
        PRODUCT = new ImmutableProduct();
        MENU_PRODUCT = new ImmutableMenuProduct(PRODUCT);
        MENU = new ImmutableMenu(List.of(MENU_PRODUCT));
    }

    private Fixture() {
    }
}
