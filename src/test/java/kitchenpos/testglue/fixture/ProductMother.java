package kitchenpos.testglue.fixture;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import kitchenpos.domain.Product;

public class ProductMother {

	private static final Map<String, Product> values = new HashMap<>();

	static {
		values.put("정상상품", 정상상품());
		values.put("가격이 빈 상품", 가격이_빈_상품());
		values.put("가격이 음수인 상품", 가격이_음수인_상품());
	}

	public static Product findByName(String name) {
		return values.get(name);
	}

	private static Product 정상상품() {
		Product product = new Product();
		product.setPrice(BigDecimal.valueOf(10000));
		product.setName("정상상품");

		return product;
	}

	private static Product 가격이_빈_상품() {
		Product product = new Product();
		product.setPrice(null);
		product.setName("가격이 빈 상품");

		return product;
	}

	private static Product 가격이_음수인_상품() {
		Product product = new Product();
		product.setPrice(BigDecimal.valueOf(-10000));
		product.setName("가격이 음수인 상품");

		return product;
	}
}
