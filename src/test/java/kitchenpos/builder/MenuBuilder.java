package kitchenpos.builder;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class MenuBuilder {
    private UUID id;
    private String name;
    private BigDecimal price;
    private MenuGroup menuGroup;
    private boolean displayed;
    private List<MenuProduct> menuProducts;
    private UUID menuGroupId;

    private MenuBuilder() {
        id = UUID.randomUUID();
        name = "후라이드치킨";
        price = BigDecimal.valueOf(16_000L);
        displayed = true;
        menuProducts = Collections.singletonList(MenuProductBuilder.newInstance()
                .build()
        );
        menuGroup = MenuGroupBuilder.newInstance()
                .build();
        menuGroupId = menuGroup.getId();
    }

    public static MenuBuilder newInstance() {
        return new MenuBuilder();
    }

    public MenuBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public MenuBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MenuBuilder setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public MenuBuilder setPrice(Long price) {
        this.price = BigDecimal.valueOf(price);
        return this;
    }

    public MenuBuilder setMenuGroup(MenuGroup menuGroup) {
        this.menuGroup = menuGroup;
        this.menuGroupId = menuGroup.getId();
        return this;
    }

    public MenuBuilder setDisplayed(boolean displayed) {
        this.displayed = displayed;
        return this;
    }

    public MenuBuilder setMenuProducts(List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts;
        return this;
    }

    public MenuBuilder setMenuProducts(MenuProduct... menuProducts) {
        this.menuProducts = Arrays.asList(menuProducts);
        return this;
    }

    public MenuBuilder setMenuGroupId(UUID menuGroupId) {
        this.menuGroupId = menuGroupId;
        return this;
    }

    public Menu build() {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroupId(menuGroupId);
        return menu;
    }
}
