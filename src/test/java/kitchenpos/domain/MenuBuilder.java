package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MenuBuilder {
    private UUID id;
    private String name;
    private BigDecimal price;
    private MenuGroup menuGroup;
    private boolean displayed;
    private List<MenuProduct> menuProducts;
    private UUID menuGroupId;

    public MenuBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public MenuBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MenuBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public MenuBuilder menuGroup(MenuGroup menuGroup) {
        this.menuGroup = menuGroup;
        return this;
    }

    public MenuBuilder displayed(boolean displayed) {
        this.displayed = displayed;
        return this;
    }

    public MenuBuilder menuProducts(List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts;
        return this;
    }

    public MenuBuilder menuGroupId(UUID menuGroupId) {
        this.menuGroupId = menuGroupId;
        return this;
    }

    public Menu build() {
        Menu menu = new Menu();
        menu.setId(Objects.requireNonNullElseGet(id, UUID::randomUUID));
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroupId(menuGroupId);
        return menu;
    }

}
