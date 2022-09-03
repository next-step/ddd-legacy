package kitchenpos.testglue.product;

import static org.assertj.core.api.Assertions.assertThat;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.application.fixture.ProductMother;
import kitchenpos.util.testglue.TestGlueConfiguration;
import kitchenpos.util.testglue.TestGlueOperation;
import kitchenpos.util.testglue.TestGlueSupport;

@TestGlueConfiguration
public class productTetGlueConfiguration extends TestGlueSupport {

	private final ProductService productService;
	private final ProductRepository productRepository;

	public productTetGlueConfiguration(
		ProductService productService,
		ProductRepository productRepository
	) {
		this.productService = productService;
		this.productRepository = productRepository;
	}

	@TestGlueOperation("{} 데이터를 생성하고")
	public void create(String name) {
		Product 상품 = ProductMother.findByName(name);

		put(name, 상품);
	}

	@TestGlueOperation("{} 생성을 요청하면")
	public void create_request(String name) {
		try {
			Product product = productService.create(getAsType(name, Product.class));
			put(name, product);
		} catch (Exception ignore) {
		}
	}

	@TestGlueOperation("{}이 생성된다")
	public void create_success(String name) {
		Product 상품 = getAsType(name, Product.class);
		assertThat(productRepository.findById(상품.getId())).isNotEmpty();
	}

	@TestGlueOperation("{}이 생성에 실패한다")
	public void create_fail(String name) {
		Product 상품 = getAsType(name, Product.class);
		assertThat(상품.getId()).isNull();
	}
}
