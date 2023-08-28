package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuGroupFixture.MENU_GROUP;
import static kitchenpos.fixture.MenuProductFixture.MENU_PRODUCT;

public class MenuFixture {

    private static String MENU_NAME = "메뉴이름1";

    public static Menu MENU() {
        MenuGroup menuGroup = MENU_GROUP();
        MenuProduct menuProduct = MENU_PRODUCT();
        Menu menu = new Menu();

        menu.setId(UUID.randomUUID());
        menu.setName(MENU_NAME);
        menu.setPrice(new BigDecimal(10_000));
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setMenuGroupId(menuGroup.getId());
        return menu;
    }
}
