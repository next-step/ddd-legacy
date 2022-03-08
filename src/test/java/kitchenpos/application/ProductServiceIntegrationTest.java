package kitchenpos.application;

import kitchenpos.IntegrationTest;
import kitchenpos.argument.NullAndNegativeBigDecimalArgumentsProvider;
import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.ProductFixture;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ProductServiceIntegrationTest extends IntegrationTest {
	private static final String PRODUCT_NAME = "product";
	private static final BigDecimal PRODUCT_PRICE = new BigDecimal(17000);
	private static final BigDecimal CHANGED_PRODUCT_SIZE = new BigDecimal(18000);
	private static final BigDecimal MENU_HIGH_PRICE = new BigDecimal(100000000);
	private static final String PROFANITY = "fuck";

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
	void createProduct() {
		// given
		Product product = new Product();
		product.setName(PRODUCT_NAME);
		product.setPrice(PRODUCT_PRICE);

		// when
		Product actualProduct = productService.create(product);

		// then
		assertAll(
			() -> assertThat(actualProduct.getId()).isNotNull(),
			() -> assertThat(actualProduct.getName()).isEqualTo(PRODUCT_NAME),
			() -> assertThat(actualProduct.getPrice()).isEqualTo(PRODUCT_PRICE)
		);
	}

	@DisplayName("가격이 null이거나 음수이면 상품 생성에 실패한다")
	@ParameterizedTest
	@ArgumentsSource(NullAndNegativeBigDecimalArgumentsProvider.class)
	void failCreatingProductWhenPriceNullOrNagativeBigDecimal(BigDecimal price) {
		// given
		Product product = new Product();
		product.setName(PRODUCT_NAME);
		product.setPrice(price); // null or negative

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> productService.create(product);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("상품이름이 빈값이면 상품 생성 실패한다")
	@Test
	void failCreatingProductWhenNameIsNull() {
		// given
		Product product = new Product();
		product.setName(null);
		product.setPrice(PRODUCT_PRICE);

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> productService.create(product);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("이름이 비속어이면 상품 생성 실패한다")
	@Test
	void failCreatingProductWhenNameIsProfanity() {
		// given
		Product product = new Product();
		product.setName(PROFANITY);
		product.setPrice(PRODUCT_PRICE);

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> productService.create(product);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("전체 상품 조회")
	@Test
	void readProduct() {
		// given
		Product givenProduct = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));

		// when
		List<Product> actual = productService.findAll();

		// then
		List<UUID> actualIds = actual.stream().map(Product::getId).collect(Collectors.toList());

		assertAll(
				() -> Assertions.assertThat(actual).isNotEmpty(),
				() -> Assertions.assertThat(actualIds).contains(givenProduct.getId())
		);
	}

	@DisplayName("상품 가격 변경")
	@Test
	void changeProductPrice() {
		// given
		Product product = ProductFixture.PRODUCT(PRODUCT_PRICE);
		productRepository.save(product);

		Product newProduct = new Product();
		newProduct.setPrice(CHANGED_PRODUCT_SIZE);

		// when
		Product actualProduct = productService.changePrice(product.getId(), newProduct);

		// then
		assertThat(actualProduct.getPrice()).isEqualTo(CHANGED_PRODUCT_SIZE);
	}

	@DisplayName("메뉴의 가격 > 메뉴 상품들의 (가격 * 수량) 일 경우 메뉴를 비노출 한다")
	@Test
	void hideProductWhenChangeProductPrice() {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Product product = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_HIGH_PRICE, menuGroup, product));

		Product newProduct = new Product();
		newProduct.setPrice(CHANGED_PRODUCT_SIZE);

		// when
		productService.changePrice(product.getId(), newProduct);

		// then
		Menu retrievedMenu = menuRepository.findById(menu.getId()).get();
		Assertions.assertThat(retrievedMenu.isDisplayed()).isFalse();
	}

	@DisplayName("가격이 널 또는 음수이면 상품 가격 변경 실패")
	@ParameterizedTest
	@ArgumentsSource(NullAndNegativeBigDecimalArgumentsProvider.class)
	void failChangingProductPriceWhenPriceNullOrNegativeDecimal(BigDecimal price) {
		// given
		Product product = ProductFixture.PRODUCT(PRODUCT_PRICE);
		productRepository.save(product);

		Product newProduct = new Product();
		newProduct.setPrice(price); // null or negative

		// when
		ThrowableAssert.ThrowingCallable throwingCallable =
			() -> productService.changePrice(product.getId(), newProduct);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}


}
