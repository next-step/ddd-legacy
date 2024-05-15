package fixture;

import java.math.BigDecimal;
import java.util.UUID;

import kitchenpos.domain.Product;

public class ProductFixture {
	public static final String VALID_PRODUCT_NAME = "치즈 버거";

	public static final String PRODUCT_NAME_WITH_PROFANITY = "비속어가 포함된 상품명";

	public static final BigDecimal VALID_PRODUCT_PRICE = new BigDecimal("8.99");

	public static Product create(String name, BigDecimal price) {
		Product product = new Product();
		product.setId(UUID.randomUUID());
		product.setName(name);
		product.setPrice(price);
		return product;
	}
}
