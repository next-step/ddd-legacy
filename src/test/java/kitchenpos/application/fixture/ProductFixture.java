package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;

public class ProductFixture {

  public static Product createFake(String name, Long price) {
    Product product = create(name, price);
    product.setId(UUID.randomUUID());
    return product;
  }

  public static Product create(String name, Long price) {
    Product product = new Product();
    product.setName(name);
    product.setPrice(new BigDecimal(price));
    return product;
  }
}
