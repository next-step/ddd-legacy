package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static kitchenpos.KitchenposFixture.ID;

public class ProductFixture {

  private static final String PRODUCT_NAME = "product name";
  private static final BigDecimal PRICE_10000 = BigDecimal.valueOf(10000L);
  private static final BigDecimal PRICE_20000 = BigDecimal.valueOf(10000L);
  private static final BigDecimal PRICE_MINUS = BigDecimal.valueOf(-10000L);

  public static Product 정상_상품() {
    Product product = new Product();
    product.setId(ID);
    product.setName(PRODUCT_NAME);
    product.setPrice(PRICE_10000);
    return product;
  }

  public static Product 상품_가격_만원() {
    Product product = new Product();
    product.setPrice(PRICE_10000);
    return product;
  }

  public static Product 상품_가격_이만원() {
    Product product = new Product();
    product.setPrice(PRICE_20000);
    return product;
  }

  public static Product 상품_가격_음수() {
    Product product = new Product();
    product.setId(ID);
    product.setName(PRODUCT_NAME);
    product.setPrice(PRICE_MINUS);
    return product;
  }

  public static List<Product> 상품_리스트_가격_만원() {
    return Collections.singletonList(상품_가격_만원());
  }


}
