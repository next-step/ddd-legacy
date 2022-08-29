package kitchenpos.stub;

import java.math.BigDecimal;
import java.util.UUID;

import kitchenpos.domain.Product;

public class ProductStub {

	private static final String DEFAULT_PRODUCT_NAME = "기본 상품";
	private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(1000);

	private ProductStub() {
	}

	public static Product createDefault() {
		return createCustom(DEFAULT_PRODUCT_NAME, DEFAULT_PRICE);
	}

	public static Product createCustom(String name, BigDecimal price) {
		Product product = new Product();
		product.setId(UUID.randomUUID());
		product.setName(name);
		product.setPrice(price);
		return product;
	}
}
