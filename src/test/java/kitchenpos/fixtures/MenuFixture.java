package kitchenpos.fixtures;

import java.math.BigDecimal;
import kitchenpos.domain.Menu;

public class MenuFixture {
  private final Menu menu;

  public MenuFixture(
      final String name,
      final BigDecimal price,
      final MenuGroupFixture menuGroupFixture,
      final MenuProductsFixture menuProductsFixture) {
    this.menu = this.ofFixture(name, price, menuGroupFixture, menuProductsFixture);
  }

  private Menu ofFixture(
      final String name,
      final BigDecimal price,
      final MenuGroupFixture menuGroupFixture,
      final MenuProductsFixture menuProductsFixture) {
    Menu menu = new Menu();
    menu.setName(name);
    menu.setPrice(price);
    menu.setMenuGroupId(menuGroupFixture.getMenuGroup().getId());

    menu.setMenuGroup(menuGroupFixture.getMenuGroup());
    menu.setMenuProducts(
        menuProductsFixture.getMenuProductsFixture().stream()
            .map(MenuProductFixture::getMenuProduct)
            .toList());
    return menu;
  }

  public Menu getMenu() {
    return menu;
  }
}
