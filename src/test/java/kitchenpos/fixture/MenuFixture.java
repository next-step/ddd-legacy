package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public class MenuFixture {
  public static Menu createMenu(final String name, final long price, final MenuGroup menuGroup, final Product product, final int quantity) {
    Menu menu = new Menu();
    menu.setName(name);
    menu.setPrice(BigDecimal.valueOf(price));
    menu.setMenuGroupId(menuGroup.getId());
    menu.setDisplayed(true);

    MenuProduct menuProduct = new MenuProduct();
    menuProduct.setProduct(product);
    menuProduct.setQuantity(quantity);
    menuProduct.setProductId(product.getId());

    menu.setMenuProducts(List.of(menuProduct));

    return menu;
  }
}
