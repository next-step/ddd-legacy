package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {
    private MenuFixture() {
    }

    public static Menu create(BigDecimal price, List<MenuProduct> menuProducts) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("메뉴");
        menu.setMenuProducts(menuProducts);
        menu.setPrice(price);

        return menu;
    }
}
