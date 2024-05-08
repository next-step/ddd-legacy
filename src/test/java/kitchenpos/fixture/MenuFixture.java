package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {
    static public Menu menuPriceAndMenuProductResponse(BigDecimal price, MenuProduct...menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(price);
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(menuProducts));
        return menu;
    }
}
