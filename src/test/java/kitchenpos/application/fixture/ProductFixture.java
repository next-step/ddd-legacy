package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;

public class ProductFixture {
  public static Product create(String name, Long price) {
    Product product = new Product();
    product.setId(UUID.randomUUID());
    product.setName(name);
    product.setPrice(new BigDecimal(price));
    return product;
  }
}
