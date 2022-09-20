package kitchenpos.fixtures;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuFixtures {

  public static Menu createMenu(
      String name,
      BigDecimal price,
      boolean isDisplayed,
      MenuGroup menuGroup,
      List<MenuProduct> menuProducts
  ) {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setName(name);
    menu.setPrice(price);
    menu.setDisplayed(isDisplayed);
    menu.setMenuGroupId(menuGroup.getId());
    menu.setMenuGroup(menuGroup);
    menu.setMenuProducts(menuProducts);
    return menu;
  }

  public static MenuGroup createMenuGroup(String name) {
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setId(UUID.randomUUID());
    menuGroup.setName(name);
    return menuGroup;
  }

  public static MenuProduct createMenuProduct(Product product, int quantity) {
    MenuProduct menuProduct = new MenuProduct();
    menuProduct.setProductId(product.getId());
    menuProduct.setProduct(product);
    menuProduct.setQuantity(quantity);
    return menuProduct;
  }

  public static Product createProduct(String name, BigDecimal price) {
    Product product = new Product();
    product.setId(UUID.randomUUID());
    product.setName(name);
    product.setPrice(price);
    return product;
  }
}
