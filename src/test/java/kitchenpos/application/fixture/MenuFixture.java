package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuFixture {
  public static MenuProduct createMenuProduct(Product product, Long quantity, Long seq) {
    MenuProduct menuProduct = new MenuProduct();
    menuProduct.setProduct(product);
    menuProduct.setQuantity(quantity);
    menuProduct.setProductId(product.getId());
    menuProduct.setSeq(seq);
    return menuProduct;
  }

  public static Menu createMenu(String name, MenuGroup menuGroup, List<MenuProduct> menuProducts, Long price, boolean displayed) {
    Menu request = new Menu();
    request.setName(name);
    request.setMenuGroup(menuGroup);
    request.setMenuProducts(menuProducts);
    request.setPrice(new BigDecimal(price));
    request.setDisplayed(displayed);
    request.setMenuGroupId(menuGroup.getId());
    return request;
  }
}
