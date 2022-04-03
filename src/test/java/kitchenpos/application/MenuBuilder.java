package kitchenpos.application;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

public class MenuBuilder {

    private String name;
    private BigDecimal price;
    private MenuGroup menuGroup;
    private boolean displayed;
    private List<MenuProduct> menuProducts;
    private UUID menuGroupId;

    public MenuBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MenuBuilder withPrice(long price) {
        this.price = BigDecimal.valueOf(price);
        return this;
    }

    public MenuBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public MenuBuilder withMenuGroup(MenuGroup menuGroup) {
        this.menuGroup = menuGroup;
        return this;
    }

    public MenuBuilder withDisplayed(boolean displayed) {
        this.displayed = displayed;
        return this;
    }

    public MenuBuilder withMenuProducts(List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts;
        return this;
    }

    public MenuBuilder withMenuProducts(MenuProduct... menuProducts) {
        this.menuProducts = Arrays.asList(menuProducts);
        return this;
    }

    public MenuBuilder withMenuGroupId(UUID menuGroupId) {
        this.menuGroupId = menuGroupId;
        return this;
    }

    public Menu build() {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroupId(menuGroupId);
        return menu;
    }
}
