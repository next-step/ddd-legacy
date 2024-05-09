package fixtures;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MenuBuilder {

    private UUID id = UUID.randomUUID();
    private String name = "menu name";
    private BigDecimal price = BigDecimal.ZERO;
    private MenuGroup menuGroup = new MenuGroup();
    private boolean displayed = true;
    private List<MenuProduct> menuProducts = new ArrayList<>();
    private UUID menuGroupId;

    public MenuBuilder with(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
        return this;
    }

    public MenuBuilder withMenuGroup(MenuGroup menuGroup) {
        this.menuGroup = menuGroup;
        this.menuGroupId = menuGroup.getId();
        return this;
    }

    public MenuBuilder withMenuProducts(List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts;
        return this;
    }

    public MenuBuilder withDisplayed(boolean displayed) {
        this.displayed = displayed;
        return this;
    }

    public Menu build() {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroupId);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        return menu;
    }
}
