package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.Fixture;

class ProductServiceIntegrationTest extends IntegrationTest {
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductRepository productRepository;

	@DisplayName("상품 생성")
	@Test
	void 상품_생성() {
		// given
		Product givenRequest = new Product();
		givenRequest.setName("강정치킨");
		givenRequest.setPrice(new BigDecimal(17000));

		// when
		Product actualProduct = productService.create(givenRequest);

		// then
		assertThat(actualProduct.getId()).isNotNull();
		assertThat(actualProduct.getName()).isEqualTo("강정치킨");
		assertThat(actualProduct.getPrice()).isEqualTo(new BigDecimal(17000));
	}

	@DisplayName("상품 생성 실패 : 가격 음수")
	@Test
	void 상품_생성_실패_1() {
		// given
		Product givenRequest = new Product();
		givenRequest.setName("강정치킨");
		givenRequest.setPrice(new BigDecimal(-10000));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> productService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("상품 생성 실패 : 이름 빈값")
	@Test
	void 상품_생성_실패_2() {
		// given
		Product givenRequest = new Product();
		givenRequest.setName(null);
		givenRequest.setPrice(new BigDecimal(17000));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> productService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("상품 생성 실패 : 이름 비속어")
	@Test
	void 상품_생성_실패_3() {
		// given
		Product givenRequest = new Product();
		givenRequest.setName("fuck");
		givenRequest.setPrice(new BigDecimal(17000));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> productService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("상품 가격 변경")
	@Test
	void 상품_가격_변경() {
		// given
		Product givenProduct = Fixture.product();
		productRepository.save(givenProduct);

		Product givenRequest = new Product();
		givenRequest.setPrice(new BigDecimal(18000));

		// when
		Product actualProduct = productService.changePrice(givenProduct.getId(), givenRequest);

		// then
		assertThat(actualProduct.getPrice()).isEqualTo(new BigDecimal(18000));
	}

	@DisplayName("상품 가격 변경 실패 : 가격 음수")
	@Test
	void 상품_가격_변경_실패_1() {
		// given
		Product givenProduct = Fixture.product();
		productRepository.save(givenProduct);

		Product givenRequest = new Product();
		givenRequest.setPrice(new BigDecimal(-10000));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> productService.changePrice(givenProduct.getId(), givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("전체 상품 조회")
	@Test
	void 전체_상품_조회() {
		// given
		productRepository.save(Fixture.product());

		// when
		List<Product> actual = productService.findAll();

		// then
		Assertions.assertThat(actual).isNotEmpty();
	}
}