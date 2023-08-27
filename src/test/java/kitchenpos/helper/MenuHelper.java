package kitchenpos.helper;


import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class MenuHelper {

    public static final String DEFAULT_NAME = "테스트 기본 메뉴명";
    public static final BigDecimal DEFAULT_PRICE = new BigDecimal(1000);
    public static final boolean DEFAULT_DISPLAYED = true;

    private MenuHelper() {
    }

    public static Menu create(BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProducts) {
        return create(price, menuGroupId, menuProducts, DEFAULT_NAME, DEFAULT_DISPLAYED);
    }

    public static Menu create(BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProducts, String name) {
        return create(price, menuGroupId, menuProducts, name, DEFAULT_DISPLAYED);
    }

    public static Menu create(BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProducts, String name, boolean displayed) {
        Menu menu = new Menu();
        menu.setPrice(price);
        menu.setId(UUID.randomUUID());
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProducts);
        menu.setName(name);
        menu.setDisplayed(displayed);
        return menu;
    }

}
