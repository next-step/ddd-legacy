package kitchenpos.fixtures;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import kitchenpos.domain.MenuProduct;

public class MenuProductFixture {
  private static final AtomicLong atomicInteger = new AtomicLong(1);
  private final MenuProduct menuProduct;

  public MenuProductFixture(final ProductFixture productFixture, final int quantity) {
    this.menuProduct = ofFixture(productFixture, quantity);
  }

  public static MenuProduct ofFixture(final ProductFixture productFixture, final int quantity) {
    final MenuProduct menuProduct = new MenuProduct();
    menuProduct.setSeq(atomicInteger.incrementAndGet());
    menuProduct.setProductId(productFixture.getProduct().getId());
    menuProduct.setProduct(productFixture.getProduct());
    menuProduct.setQuantity(quantity);
    return menuProduct;
  }

  public MenuProduct getMenuProduct() {
    return menuProduct;
  }
}
