package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.List;

public class MenuRequestBuilder {

    public static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(10_000L);
    public static final String DEFAULT_NAME = "메뉴 이름";
    public static final boolean DEFAULT_DISPLAYED = true;

    private MenuGroup menuGroup;
    private List<MenuProduct> menuProducts;
    private BigDecimal price = DEFAULT_PRICE;
    private String name = DEFAULT_NAME;
    private boolean displayed = DEFAULT_DISPLAYED;

    public MenuRequestBuilder(MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        this.menuGroup = menuGroup;
        this.menuProducts = menuProducts;
    }

    public MenuRequestBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public MenuRequestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MenuRequestBuilder withDisplayed(boolean displayed) {
        this.displayed = displayed;
        return this;
    }

    public Menu build() {
        Menu menu = new Menu();
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        menu.setPrice(price);
        menu.setName(name);
        menu.setDisplayed(displayed);
        return menu;
    }

    public MenuRequestBuilder withMenuProducts(List<MenuProduct> menuProductRequests) {
        this.menuProducts = menuProductRequests;
        return this;
    }
}
