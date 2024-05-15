package kitchenpos.fixtures;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import kitchenpos.domain.Product;

public class ProductsFixture {
  private final List<ProductFixture> productFixtures;

  public ProductsFixture(List<ProductFixture> productFixtures) {
    this.productFixtures = productFixtures;
  }

  public static ProductsFixture create() {
    return new ProductsFixture(
        Arrays.asList(
            new ProductFixture("제육덮밥", BigDecimal.valueOf(20_000L)),
            new ProductFixture("김치찌개", BigDecimal.valueOf(25_000L)),
            new ProductFixture("알탕", BigDecimal.valueOf(21_000L)),
            new ProductFixture("마른 오징어", BigDecimal.valueOf(11_000L))));
  }

  public List<ProductFixture> getList() {
    return productFixtures;
  }

  public List<Product> getProductList() {
    return this.productFixtures.stream().map(ProductFixture::getProduct).toList();
  }
}
