package kitchenpos.fixture;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

public class MenuFixture {

    private MenuFixture() {
    }

    public static Menu create(
        UUID id,
        String name,
        BigDecimal price,
        MenuGroup menuGroup,
        boolean displayed,
        List<MenuProduct> menuProducts
    ) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);

        return menu;
    }

    public static Menu create(int price, List<MenuProduct> menuProducts) {
        return create(
            UUID.randomUUID(), "testMenu",
            BigDecimal.valueOf(price),
            MenuGroupFixture.create("testMenuGroup"),
            true,
            menuProducts
        );
    }

    public static Menu create(List<MenuProduct> menuProducts) {
        return create(20_000, menuProducts);
    }

    public static Menu create(int price) {
        return create(
            UUID.randomUUID(), "testMenu",
            BigDecimal.valueOf(price),
            MenuGroupFixture.create("testMenuGroup"),
            true,
            List.of(MenuProductFixture.create())
        );
    }

    public static Menu create(String name) {
        return create(
            UUID.randomUUID(), name,
            BigDecimal.valueOf(10_000),
            MenuGroupFixture.create("testMenuGroup"),
            true,
            List.of(MenuProductFixture.create())
        );
    }

    public static Menu create(boolean displayed) {
        return create(
            UUID.randomUUID(), "testMenu",
            BigDecimal.valueOf(10_000),
            MenuGroupFixture.create("testMenuGroup"),
            displayed,
            List.of(MenuProductFixture.create())
        );
    }


    public static Menu create() {
        return create(10_000);
    }
}
