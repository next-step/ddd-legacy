package kitchenpos.helper;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MenuTestHelper {
    public static Menu 메뉴_생성(MenuGroup menuGroup, String name, BigDecimal price, List<MenuProduct> menuProducts){
        Menu menu = new Menu();
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);

        return menu;
    }
}
