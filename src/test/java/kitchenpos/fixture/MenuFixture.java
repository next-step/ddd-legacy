package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(10_000L);

    private MenuFixture() {
    }

    public static Menu 기본_메뉴() {
        return 메뉴_생성(DEFAULT_PRICE, List.of(MenuProductFixture.기본_메뉴_상품()));
    }

    public static Menu 그룹_없는_메뉴() {
        Menu menu = 메뉴_생성(DEFAULT_PRICE, List.of(MenuProductFixture.기본_메뉴_상품()));
        menu.setMenuGroup(null);

        return menu;
    }

    public static Menu 메뉴_생성(BigDecimal price) {
        return 메뉴_생성(price, List.of(MenuProductFixture.기본_메뉴_상품()));
    }

    public static Menu 메뉴_생성(String name) {
        return 메뉴_생성(DEFAULT_PRICE, List.of(MenuProductFixture.기본_메뉴_상품()), name);
    }

    public static Menu 메뉴_생성(List<MenuProduct> menuProducts) {
        return 메뉴_생성(DEFAULT_PRICE, menuProducts);
    }

    public static Menu 메뉴_생성(BigDecimal price, List<MenuProduct> menuProducts) {
        return 메뉴_생성(price, menuProducts, "테스트 메뉴");
    }

    public static Menu 메뉴_생성(BigDecimal price, List<MenuProduct> menuProducts, String name) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setMenuProducts(menuProducts);
        menu.setPrice(price);
        menu.setMenuGroup(기본_메뉴_그룹());

        return menu;
    }

    public static MenuGroup 기본_메뉴_그룹() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("메뉴그룹");

        return menuGroup;
    }

    public static MenuGroup 메뉴_그룹_생성(String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);

        return menuGroup;
    }
}
