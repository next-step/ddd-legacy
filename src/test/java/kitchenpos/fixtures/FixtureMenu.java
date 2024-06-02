package kitchenpos.fixtures;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class FixtureMenu {
  public static MenuGroup fixtureMenuGroup() {
    final MenuGroup menuGroup = new MenuGroup();
    menuGroup.setId(UUID.randomUUID());
    menuGroup.setName("치킨세트");
    return menuGroup;
  }

  public static Menu fixtureMenu() {
    final Menu menu = new Menu();
    final MenuGroup menuGroup = FixtureMenu.fixtureMenuGroup();
    menu.setName("치킨");
    menu.setPrice(BigDecimal.valueOf(28_000L));
    menu.setMenuGroupId(menuGroup.getId());
    menu.setMenuGroup(menuGroup);
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
