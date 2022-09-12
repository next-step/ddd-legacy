package kitchenpos.application.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {

  public static MenuProduct createMenuProduct() {
    return createMenuProduct(2, ProductFixture.createProduct());
  }

  public static MenuProduct createMenuProduct(int quantity, Product product) {
    MenuProduct menuProduct = new MenuProduct();
    menuProduct.setQuantity(quantity);
    menuProduct.setProduct(product);

    return menuProduct;
  }
}
