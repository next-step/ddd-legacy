package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.domain.MenuProductFixture.*;

public class MenuFixture {

    public static final Menu CHICKEN_MENU =
        MenuFixture.builder()
                   .id(UUID.randomUUID())
                   .name("치킨 메뉴")
                   .price(BigDecimal.valueOf(38000L))
                   .displayed(true)
                   .menuProducts(Arrays.asList(MP_FRIED_CHICKEN, MP_HONEY_COMBO))
                   .build();

    private UUID id;
    private String name;
    private BigDecimal price;
    private boolean displayed;
    private List<MenuProduct> menuProducts;
    private UUID menuGroupId;

    public static MenuFixture builder() {
        return new MenuFixture();
    }

    public MenuFixture id(UUID id) {
        this.id = id;
        return this;
    }

    public MenuFixture name(String name) {
        this.name = name;
        return this;
    }

    public MenuFixture price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public MenuFixture displayed(boolean displayed) {
        this.displayed = displayed;
        return this;
    }

    public MenuFixture menuProducts(List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts;
        return this;
    }

    public MenuFixture menuGroupId(UUID menuGroupId) {
        this.menuGroupId = menuGroupId;
        return this;
    }

    public Menu build() {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroupId(menuGroupId);
        return menu;
    }
}
