package kitchenpos.testglue.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.application.ProductService;
import kitchenpos.application.fixture.ProductMother;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.util.testglue.TestGlueConfiguration;
import kitchenpos.util.testglue.TestGlueOperation;
import kitchenpos.util.testglue.TestGlueSupport;
import kitchenpos.util.testglue.test.TestGlueResponse;

@TestGlueConfiguration
public class ProductTestGlueConfiguration extends TestGlueSupport {

	private final ProductService productService;
	private final ProductRepository productRepository;

	public ProductTestGlueConfiguration(
		ProductService productService,
		ProductRepository productRepository
	) {
		this.productService = productService;
		this.productRepository = productRepository;
	}

	@TestGlueOperation("{} 상품 데이터를 생성하고")
	public void create_data(String name) {
		Product 상품 = ProductMother.findByName(name);

		put(name, 상품);
	}

	@TestGlueOperation("{} 상품 생성을 요청하면")
	public void create_request(String name) {

		TestGlueResponse<Product> response = createResponse(() -> productService.create(getAsType(name, Product.class)));

		put(name, response);
	}

	@TestGlueOperation("{} 상품이 생성된다")
	public void create_success(String name) {
		TestGlueResponse<Product> response = getAsType(name, TestGlueResponse.class);

		Product product = response.getData();

		assertThat(productRepository.findById(product.getId())).isNotEmpty();
	}

	@TestGlueOperation("{} 상품이 생성에 실패한다")
	public void create_fail(String name) {
		TestGlueResponse<Product> response = getAsType(name, TestGlueResponse.class);

		assertThat(response.isOk()).isFalse();
	}

	@TestGlueOperation("{} 상품을 생성하고")
	public void create(String name) {
		Product product = ProductMother.findByName(name);
		put(name, productService.create(product));
	}

	@TestGlueOperation("{} 상품 가격을 {} 으로 변경하면")
	public void changePrice(String name, String price) {
		Product product = getAsType(name, Product.class);
		BigDecimal bigDecimalPrice = toBigDecimal(price);
		product.setPrice(bigDecimalPrice);

		put("changedPrice", bigDecimalPrice);

		TestGlueResponse<Product> response = createResponse(() -> productService.changePrice(product.getId(), product));
		put("response", response);
	}

	@TestGlueOperation("존재하지 않는 상품 상품 가격을 {} 으로 변경하면")
	public void notExistProduct_changePrice(String price) {
		Product product = new Product();
		product.setPrice(toBigDecimal(price));
		TestGlueResponse<Product> response = createResponse(() -> productService.changePrice(UUID.randomUUID(), product));

		put("response", response);
	}

	@TestGlueOperation("{} 상품 가격은 변경된다")
	public void changePrice_result(String name) {
		TestGlueResponse<Product> changePriceResponse = getAsType("response", TestGlueResponse.class);
		assertThat(changePriceResponse.isOk()).isTrue();

		Product product = getAsType(name, Product.class);
		Product savedProduct = productRepository.findById(product.getId()).orElseThrow();
		BigDecimal changedPrice = getAsType("changedPrice", BigDecimal.class);

		assertThat(savedProduct.getPrice().longValue()).isEqualTo(changedPrice.longValue());
	}

	@TestGlueOperation("{} 상품 가격 변경에 실패한다")
	public void changePrice_result_fail(String name) {
		TestGlueResponse<Product> changePriceResponse = getAsType("response", TestGlueResponse.class);
		assertThat(changePriceResponse.isOk()).isFalse();

		Product product = getAsType(name, Product.class);
		Product savedProduct = productRepository.findById(product.getId()).orElseThrow();

		assertThat(savedProduct.getPrice()).isNotEqualTo(product.getPrice());
	}

	@TestGlueOperation("존재하지 않는 상품 상품 가격 변경에 실패한다")
	public void notExistProduct_changePrice_result() {
		TestGlueResponse<Product> changePriceResponse = getAsType("response", TestGlueResponse.class);
		assertThat(changePriceResponse.isOk()).isFalse();
	}

	private BigDecimal toBigDecimal(String price) {
		try {
			return BigDecimal.valueOf(Long.parseLong(price));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
