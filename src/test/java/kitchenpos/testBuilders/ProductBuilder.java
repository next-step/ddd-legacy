package kitchenpos.testBuilders;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductBuilder {
	public static final String DEFAULT_PRODUCT_NAME = "짬뽕";
	public static final BigDecimal DEFAULT_PRODUCT_PRICE = BigDecimal.valueOf(1000L);
	public static final BigDecimal MORE_CHEAPER_PRODUCT_PRICE_THAN_DEFAULT_PRODUCT_PRICE = DEFAULT_PRODUCT_PRICE.subtract(BigDecimal.ONE);
	private UUID id;
	private String name;
	private BigDecimal price;

	private ProductBuilder() {
	}

	public static ProductBuilder aProduct() {
		return new ProductBuilder();
	}

	public static ProductBuilder aDefaultProduct() {
		return aProduct()
				.withId(UUID.randomUUID())
				.withPrice(DEFAULT_PRODUCT_PRICE)
				.withName(DEFAULT_PRODUCT_NAME);
	}

	public ProductBuilder withId(UUID id) {
		this.id = id;
		return this;
	}

	public ProductBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public ProductBuilder withPrice(BigDecimal price) {
		this.price = price;
		return this;
	}

	public Product build() {
		Product product = new Product();
		product.setId(id);
		product.setPrice(price);
		product.setName(name);
		return product;
	}
}
