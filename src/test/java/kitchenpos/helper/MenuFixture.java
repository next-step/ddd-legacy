package kitchenpos.helper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

public class MenuFixture {

    public static final Menu ONE_FRIED_CHICKEN = create(
        "후라이드 치킨 한마리",
        6000,
        true,
        MenuGroupFixture.CHICKEN,
        List.of(MenuProductFixture.ONE_FRIED_CHICKEN)
    );

    public static final Menu TWO_FRIED_CHICKEN = create(
        "후라이드 치킨 두마리",
        9000,
        true,
        MenuGroupFixture.CHICKEN,
        List.of(MenuProductFixture.TWO_FRIED_CHICKEN)
    );

    public static final Menu NO_DISPLAYED_MENU = create(
        "양념 치킨 한마리",
        6000,
        false,
        MenuGroupFixture.CHICKEN,
        List.of(MenuProductFixture.ONE_HOT_SPICY_CHICKEN)
    );

    public static final Menu DISPLAYED_MENU = ONE_FRIED_CHICKEN;

    private static Menu create(
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

    public static Menu request(
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

    public static Menu request(BigDecimal price) {
        var request = new Menu();
        request.setPrice(price);
        return request;
    }
}
