package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuGroupFixture.menuGroup;
import static kitchenpos.fixture.MenuProductFixture.menuProduct;
import static kitchenpos.fixture.ProductFixture.product;

public class MenuFixture {

    private static final String DEFAULT_MENU_NAME = "양념 후라이드 세트";
    private static final BigDecimal DEFAULT_MENU_PRICE = BigDecimal.valueOf(30_000L);

    private MenuFixture() {
    }

    public static Menu menu() {
        return menu(uuid(), DEFAULT_MENU_NAME, DEFAULT_MENU_PRICE,
                menuGroup(uuid(), "세트 메뉴"),
                List.of(
                        menuProduct(1L, 1L, product(uuid(), "양념 치킨", 16_000)),
                        menuProduct(2L, 1L, product(uuid(), "후라이드 치킨", 16_000))
                ),
                true);
    }

    public static Menu menu(final UUID id, final String name, final BigDecimal price,
                            final MenuGroup menuGroup, final List<MenuProduct> menuProducts, final boolean isDisplayed) {
        final Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(isDisplayed);
        return menu;
    }

    private static UUID uuid() {
        return UUID.randomUUID();
    }
}
