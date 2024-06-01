package kitchenpos.fixtures;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class FixtureMenu {
  public static MenuGroup fixtureMenuGroup() {
    final MenuGroup menuGroup = new MenuGroup();
    menuGroup.setId(UUID.randomUUID());
    menuGroup.setName("치킨세트");
    return menuGroup;
  }

  public static Menu fixtureMenu() {
    final Menu menu = new Menu();
    menu.setName("치킨");
    menu.setPrice(BigDecimal.valueOf(28_000L));
    menu.setMenuGroupId(UUID.randomUUID());
    menu.setMenuGroup(FixtureMenu.fixtureMenuGroup());
    menu.setMenuProducts(FixtureMenu.fixtureMenuProducts());
    menu.setDisplayed(true);
    return menu;
  }

  public static MenuProduct fixtureMenuProduct() {
    final MenuProduct menuProduct = new MenuProduct();
    final Product product = FixtureProduct.fixtureProduct();
    menuProduct.setProduct(product);
    menuProduct.setProductId(product.getId());
    menuProduct.setSeq(1L);
    menuProduct.setQuantity(1L);
    return menuProduct;
  }

  public static List<MenuProduct> fixtureMenuProducts() {
    final MenuProduct menuProduct = FixtureMenu.fixtureMenuProduct();
    return List.of(menuProduct);
  }
}
