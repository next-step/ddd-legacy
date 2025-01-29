package kitchenpos.application.fixture;

import io.micrometer.common.util.StringUtils;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {
    public static Menu setMenuGroup(MenuGroup menuGroup, String name, String price, List<MenuProduct> products) {
        Menu menu = new Menu();

        menu.setMenuGroup(menuGroup);
        if(menuGroup != null){
            menu.setMenuGroupId(menuGroup.getId());
        }
        menu.setName(name);
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        if(!StringUtils.isBlank(price)){
            menu.setPrice(BigDecimal.valueOf(Double.parseDouble(price)));
        }
        menu.setMenuProducts(products);

        return menu;
    }
}
