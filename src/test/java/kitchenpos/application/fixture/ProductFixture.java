package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;

public class ProductFixture {

  public static Product createProduct() {
    return createProduct("강정치킨", 17_000L);
  }

  public static Product createProduct(String name, long price) {
    Product product = new Product();
    product.setId(UUID.randomUUID());
    product.setName(name);
    product.setPrice(BigDecimal.valueOf(price));
    return product;
  }
}
