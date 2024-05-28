package kitchenpos.fixture;

import kitchenpos.application.product.FakeUuidBuilder;
import kitchenpos.common.UuidBuilder;
import kitchenpos.domain.Product;

import java.math.BigDecimal;

public class ProductFixture {

  public static Product createProduct(final String name, final long price, final UuidBuilder uuidBuilder) {
    final UuidBuilder fakeUuidBuilder = uuidBuilder;
    final Product product = new Product(fakeUuidBuilder);

    product.setPrice(BigDecimal.valueOf(price));
    product.setName(name);

    return product;
  }

  public static Product createProduct(final String name, final long price) {
    final UuidBuilder fakeUuidBuilder = new FakeUuidBuilder();
    final Product product = new Product(fakeUuidBuilder);

    product.setPrice(BigDecimal.valueOf(price));
    product.setName(name);

    return product;
  }

  public static Product createProduct(final long price) {
    return createProduct("한강라면", price);
  }

  public static Product createProduct(final String name) {
    return createProduct(name, 20_000L);
  }
}
