package kitchenpos.fixtures;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class FixtureProduct extends FixtureMenu {
  public static Product fixtureProduct() {
    final Product product = new Product();
    product.setId(UUID.randomUUID());
    product.setPrice(BigDecimal.valueOf(28_000L));
    product.setName("치킨");
    return product;
  }
}
