package kitchenpos.fixture;

import kitchenpos.domain.common.FakeUuidBuilder;
import kitchenpos.common.UuidBuilder;
import kitchenpos.domain.Product;

import java.math.BigDecimal;

public class ProductFixture {

  public static final String UDON = "우동";
  public static final String RAMEN = "라면";
  public static final String FOR_TWO = "이인용";
  public static final String TWO_UDONS = "우동과라면";
  public static final int TEN_THOUSAND = 10_000;
  public static final long TWENTY_THOUSANDS = 20_000L;
  public static final int THRITY_THOUSANDS = 30_000;
  private static FakeUuidBuilder fakeUuidBuilder = new FakeUuidBuilder();

  public static Product ramen = ProductFixture.createProduct(RAMEN, TEN_THOUSAND, fakeUuidBuilder);
  public static Product udon = ProductFixture.createProduct(UDON, TWENTY_THOUSANDS,
      fakeUuidBuilder);

  public static Product createProduct(final String name, final long price,
      final UuidBuilder uuidBuilder) {
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
