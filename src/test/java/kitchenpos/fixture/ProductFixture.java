package kitchenpos.fixture;

import java.math.BigDecimal;
import java.util.UUID;

import kitchenpos.domain.Product;

public class ProductFixture {
	public static Product PRODUCT(BigDecimal price) {
		Product product = new Product();
		product.setId(UUID.randomUUID());
		product.setName("강정치킨");
		product.setPrice(price);
		return product;
	}
}
