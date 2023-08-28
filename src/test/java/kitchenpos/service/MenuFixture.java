package kitchenpos.service;

import java.math.BigDecimal;
import java.util.UUID;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;

public class MenuFixture {
    private final Menu menu;

    public MenuFixture() {
        menu = new Menu();
        menu.setId(UUID.randomUUID());
    }

    public static MenuFixture builder() {
        return new MenuFixture();
    }

    public MenuFixture name(String name) {
        menu.setName(name);
        return this;
    }

    public MenuFixture price(BigDecimal price) {
        menu.setPrice(price);
        return this;
    }

    public MenuFixture menuGroup(MenuGroup menuGroup) {
        menu.setMenuGroup(menuGroup);
        return this;
    }

    public MenuFixture displayed(boolean displayed) {
        menu.setDisplayed(displayed);
        return this;
    }

    public Menu build() {
        return menu;
    }
}
