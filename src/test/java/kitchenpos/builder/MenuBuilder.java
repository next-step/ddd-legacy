package kitchenpos.builder;

import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuBuilder {
    private long id;
    private String name;
    private BigDecimal price;
    private long menuGroupId;
    private List<MenuProduct> menuProducts;

    private MenuBuilder() {
    }

    public static MenuBuilder create() {
        return new MenuBuilder();
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

    public MenuBuilder setId(final long id) {
        this.id = id;
        return this;
    }

    public MenuBuilder setName(final String name) {
        this.name = name;
        return this;
    }

    public MenuBuilder setPrice(final BigDecimal price) {
        this.price = price;
        return this;
    }

    public MenuBuilder setMenuGroupId(final long menuGroupId) {
        this.menuGroupId = menuGroupId;
        return this;
    }

    public MenuBuilder setMenuProducts(final List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts;
        return this;
    }
}
