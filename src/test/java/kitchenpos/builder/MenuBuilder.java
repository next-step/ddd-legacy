package kitchenpos.builder;

import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;

import java.math.BigDecimal;
import java.util.List;

public class MenuBuilder {
    private long id;
    private String name;
    private BigDecimal price;
    private Long menuGroupId;
    private List<MenuProduct> menuProducts;

    private MenuBuilder() {
    }

    public static MenuBuilder menu() {
        return new MenuBuilder();
    }

    public MenuBuilder withId(final long id) {
        this.id = id;
        return this;
    }

    public MenuBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public MenuBuilder withPrice(final BigDecimal price) {
        this.price = price;
        return this;
    }

    public MenuBuilder withMenuGroupId(final Long menuGroupId) {
        this.menuGroupId = menuGroupId;
        return this;
    }

    public MenuBuilder withMenuProducts(final List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts;
        return this;
    }

    public Menu build() {
        Menu menu = new Menu();
        menu.setId(this.id);
        menu.setName(this.name);
        menu.setPrice(this.price);
        menu.setMenuGroupId(this.menuGroupId);
        menu.setMenuProducts(this.menuProducts);
        return menu;
    }
}
