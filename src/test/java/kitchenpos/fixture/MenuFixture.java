package kitchenpos.fixture;

import kitchenpos.domain.Menu;

import java.math.BigDecimal;
import java.util.UUID;

public class MenuFixture {
    public static Menu menu(String name, long price, UUID menuGroupId) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroupId(menuGroupId);
        return menu;
    }
}
