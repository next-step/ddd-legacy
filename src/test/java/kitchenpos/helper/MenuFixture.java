package kitchenpos.helper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

public class MenuFixture {

    public static final Supplier<Menu> ONE_FRIED_CHICKEN = () -> menu(
        "후라이드 치킨 한마리",
        6000,
        true,
        MenuGroupFixture.CHICKEN,
        List.of(MenuProductFixture.ONE_FRIED_CHICKEN)
    );

    public static final Supplier<Menu> TWO_FRIED_CHICKEN = () -> menu(
        "후라이드 치킨 두마리",
        9000,
        true,
        MenuGroupFixture.CHICKEN,
        List.of(MenuProductFixture.TWO_FRIED_CHICKEN)
    );

    public static final Supplier<Menu> NO_DISPLAYED_MENU = () -> menu(
        "양념 치킨 한마리",
        6000,
        false,
        MenuGroupFixture.CHICKEN,
        List.of(MenuProductFixture.ONE_HOT_SPICY_CHICKEN)
    );

    public static final Supplier<Menu> DISPLAYED_MENU = ONE_FRIED_CHICKEN;

    public static final Supplier<Menu> PRICE_EXCEED_MENU = () -> menu(
        "양념 치킨 한마리",
        7000,
        false,
        MenuGroupFixture.CHICKEN,
        List.of(MenuProductFixture.ONE_HOT_SPICY_CHICKEN)
    );

    private static Menu menu(
        String name,
        int price,
        boolean displayed,
        MenuGroup menuGroup,
        List<MenuProduct> menuProducts
    ) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(displayed);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu menu(MenuProduct... menuProducts) {
        return menu(
            "후라이드 치킨",
            6000,
            true,
            MenuGroupFixture.CHICKEN,
            Arrays.asList(menuProducts)
        );
    }

    public static Menu createMenuRequest(
        int price,
        UUID menuGroupId,
        String name,
        boolean displayed,
        MenuProduct... menuProducts
    ) {
        var request = new Menu();
        request.setPrice(BigDecimal.valueOf(price));
        request.setMenuGroupId(menuGroupId);
        request.setName(name);
        request.setDisplayed(displayed);
        request.setMenuProducts(List.of(menuProducts));
        return request;
    }

    public static Menu createMenuRequest(int price, UUID menuGroupId, UUID productId) {
        return createMenuRequest(
            price,
            menuGroupId,
            "후라이드 치킨",
            true,
            MenuProductFixture.request(productId, 1)
        );
    }

    public static Menu createMenuRequest(UUID menuGroupId, UUID productId) {
        return createMenuRequest(6000, menuGroupId, productId);
    }

    public static Menu createMenuRequest(int price) {
        return createMenuRequest(
            price,
            MenuGroupFixture.CHICKEN.getId(),
            ProductFixture.FRIED_CHICKEN.get().getId()
        );
    }

    public static Menu createMenuRequest(String name, UUID menuGroupId, UUID productId) {
        return createMenuRequest(
            6000,
            menuGroupId,
            name,
            true,
            MenuProductFixture.request(productId, 1)
        );
    }

    public static Menu createMenuRequest(UUID menuGroupId, MenuProduct... menuProducts) {
        return createMenuRequest(
            6000,
            menuGroupId,
            "후라이드 치킨",
            true,
            menuProducts
        );
    }

    public static Menu changePriceRequest(BigDecimal price) {
        var request = new Menu();
        request.setPrice(price);
        return request;
    }

    public static Menu changePriceRequest(int price) {
        return changePriceRequest(BigDecimal.valueOf(price));
    }
}
