package kitchenpos.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Menu;

public class MenuFixture {

    public static Menu createMenu() {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(BigDecimal.valueOf(16_000L));
        menu.setDisplayed(true);
        return menu;
    }
}
