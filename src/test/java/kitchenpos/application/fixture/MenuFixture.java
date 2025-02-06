package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

public record MenuFixture(UUID id, String 메뉴명, BigDecimal 메뉴가격,
                          MenuGroup 메뉴그룹, boolean 노출여부, List<MenuProduct> 메뉴구성품) {

    private static final String DEFAULT_MENU_NAME = "황금 올리브 치킨";
    private static final BigDecimal DEFAULT_MENU_PRICE = BigDecimal.valueOf(20_000);

    public static MenuFixture init() {
        return new MenuFixture(
            UUID.randomUUID(),
            DEFAULT_MENU_NAME,
            DEFAULT_MENU_PRICE,
            MenuGroupFixture.init().create(),
            true,
            List.of(MenuProductFixture.init().create()));
    }

    public static MenuFixture test(String 메뉴명, BigDecimal 메뉴가격,
        MenuGroup 메뉴그룹, boolean 노출여부, List<MenuProduct> 메뉴구성품) {
        return new MenuFixture(
            UUID.randomUUID(),
            Objects.requireNonNullElse(메뉴명, DEFAULT_MENU_NAME),
            Objects.requireNonNullElse(메뉴가격, DEFAULT_MENU_PRICE),
            Objects.requireNonNullElse(메뉴그룹, MenuGroupFixture.init().create()),
            노출여부,
            Objects.requireNonNullElse(메뉴구성품, List.of(MenuProductFixture.init().create()))
        );
    }

    public Menu create() {
        var menu = new Menu();
        menu.setId(id);
        menu.setName(메뉴명);
        menu.setPrice(메뉴가격);
        menu.setDisplayed(노출여부);
        menu.setMenuGroup(메뉴그룹);
        menu.setMenuProducts(메뉴구성품);

        return menu;
    }
}

