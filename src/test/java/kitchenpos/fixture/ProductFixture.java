package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
	public static Product PRODUCT(BigDecimal price) {
		Product product = new Product();
		product.setId(UUID.randomUUID());
		product.setName("바삭양념");
		product.setPrice(price);
		return product;
	}
}
