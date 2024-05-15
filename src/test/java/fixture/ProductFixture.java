package fixture;

import java.math.BigDecimal;
import java.util.UUID;

import kitchenpos.domain.Product;

public class ProductFixture {
	private static final BigDecimal VALID_PRODUCT_PRICE = new BigDecimal("8.99");
	private static final String VALID_PRODUCT_NAME = "치즈 버거";

	public static Product createValidProduct() {
		Product product = new Product();
		product.setId(UUID.randomUUID());
		product.setName(VALID_PRODUCT_NAME);
		product.setPrice(VALID_PRODUCT_PRICE);
		return product;
	}
}
