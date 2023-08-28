package kitchenpos.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

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
        menu.setMenuGroupId(menuGroup.getId());
        return this;
    }

    public MenuFixture menuProduct(MenuProduct menuProduct) {
        menu.setMenuProducts(List.of(menuProduct));
        return this;
    }

    public MenuFixture menuProducts(List<MenuProduct> menuProducts) {
        menu.setMenuProducts(menuProducts);
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
