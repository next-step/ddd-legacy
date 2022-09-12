package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

public class MenuFixture {

  public static Menu createMenu() {
    return createMenu("후라이드+후라이드", 19000, MenuProductFixture.createMenuProduct());
  }

  public static Menu createMenu(String name, long price, MenuProduct... menuProducts) {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setName(name);
    menu.setPrice(BigDecimal.valueOf(price));
    menu.setDisplayed(true);
    menu.setMenuProducts(List.of(menuProducts));

    return menu;
  }
}
