package kitchenpos;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class KitchenposFixture {
  private static final String MENU_NAME = "menu name";
  private static final BigDecimal MENU_PRICE = BigDecimal.valueOf(10000);
  private static final long QUANTITY = 1L;
  private static final String PRODUCT_NAME = "product name";
  private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(10000);

  public static MenuGroup menuGroup() {
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setId(UUID.randomUUID());
    menuGroup.setName("menu group name");
    return menuGroup;
  }

  public static OrderTable orderTable() {
    OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setName("order table name");
    orderTable.setEmpty(false);
    return orderTable;
  }

  public static Menu menu() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setName(MENU_NAME);
    menu.setPrice(MENU_PRICE);
    menu.setDisplayed(true);

    List<MenuProduct> menuProductList = Collections.singletonList(menuProduct());
    menu.setMenuProducts(menuProductList);

    MenuGroup menuGroup = menuGroup();
    menu.setMenuGroup(menuGroup);
    menu.setMenuGroupId(menuGroup.getId());

    return menu;
  }

  public static MenuProduct menuProduct() {
    MenuProduct menuProduct = new MenuProduct();
    Product product = product();
    menuProduct.setProduct(product);
    menuProduct.setProductId(product.getId());
    menuProduct.setQuantity(QUANTITY);
    return menuProduct;
  }

  public static Product product() {
    Product product = new Product();
    product.setId(UUID.randomUUID());
    product.setName(PRODUCT_NAME);
    product.setPrice(PRODUCT_PRICE);
    return product;
  }


}
