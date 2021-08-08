package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.IntegrationTest;
import kitchenpos.argument.NullAndNegativeBigDecimalArgumentsProvider;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.ProductFixture;

class ProductServiceIntegrationTest extends IntegrationTest {
	@Autowired
	private ProductService productService;
	@Autowired
	private MenuGroupRepository menuGroupRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private MenuRepository menuRepository;

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
		assertAll(
			() -> assertNotNull(actualProduct.getId()),
			() -> assertEquals("강정치킨", actualProduct.getName()),
			() -> assertEquals(new BigDecimal(17000), actualProduct.getPrice())
		);
	}

	@DisplayName("상품 생성 실패 : 가격이 널 또는 음수")
	@ParameterizedTest
	@ArgumentsSource(NullAndNegativeBigDecimalArgumentsProvider.class)
	void 상품_생성_실패_1(BigDecimal price) {
		// given
		Product givenRequest = new Product();
		givenRequest.setName("강정치킨");
		givenRequest.setPrice(price); // null or negative

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
		Product givenProduct = ProductFixture.product(new BigDecimal(17000));
		productRepository.save(givenProduct);

		Product givenRequest = new Product();
		givenRequest.setPrice(new BigDecimal(18000));

		// when
		Product actualProduct = productService.changePrice(givenProduct.getId(), givenRequest);

		// then
		assertThat(actualProduct.getPrice()).isEqualTo(new BigDecimal(18000));
	}

	@DisplayName("상품 가격 비노출 : 메뉴의 가격 > 메뉴 상품들의 (가격 * 수량) 일 경우, 메뉴를 비노출한다.")
	@Test
	void 상품_가격_변경_비노출() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		Menu givenMenu = menuRepository.save(MenuFixture.menu(new BigDecimal(100000000), givenMenuGroup, givenProduct));

		Product givenRequest = new Product();
		givenRequest.setPrice(new BigDecimal(18000));

		// when
		productService.changePrice(givenProduct.getId(), givenRequest);

		// then
		Menu retrievedMenu = menuRepository.findById(givenMenu.getId()).get();
		Assertions.assertThat(retrievedMenu.isDisplayed()).isEqualTo(false);
	}

	@DisplayName("상품 가격 변경 실패 : 가격이 널 또는 음수")
	@ParameterizedTest
	@ArgumentsSource(NullAndNegativeBigDecimalArgumentsProvider.class)
	void 상품_가격_변경_실패_1(BigDecimal price) {
		// given
		Product givenProduct = ProductFixture.product(new BigDecimal(17000));
		productRepository.save(givenProduct);

		Product givenRequest = new Product();
		givenRequest.setPrice(price); // null or negative

		// when
		ThrowableAssert.ThrowingCallable throwingCallable =
			() -> productService.changePrice(givenProduct.getId(), givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("전체 상품 조회")
	@Test
	void 전체_상품_조회() {
		// given
		productRepository.save(ProductFixture.product(new BigDecimal(17000)));

		// when
		List<Product> actual = productService.findAll();

		// then
		Assertions.assertThat(actual).isNotEmpty();
	}
}
