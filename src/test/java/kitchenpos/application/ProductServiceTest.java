package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import kitchenpos.common.MockitoUnitTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.stub.MenuGroupStub;
import kitchenpos.stub.MenuProductStub;
import kitchenpos.stub.MenuStub;
import kitchenpos.stub.ProductStub;

class ProductServiceTest extends MockitoUnitTest {

	@Mock
	private PurgomalumClient purgomalumClient;
	@Mock
	private MenuRepository menuRepository;
	@Mock
	private ProductRepository productRepository;
	@InjectMocks
	private ProductService productService;

	private Product product;
	private Product newProduct;

	@BeforeEach
	void setUp() {
		product = ProductStub.createDefault();
		newProduct = ProductStub.createCustom("변경된 상품", BigDecimal.valueOf(2000));
	}

	@DisplayName("상품 등록 시")
	@Nested
	class CreateTest {

		@DisplayName("새 상품을 등록할 수 있다.")
		@Test
		void create() {
			// given
			when(productRepository.save(any()))
				.thenReturn(product);

			// when
			Product result = productService.create(product);

			//then
			assertThat(result)
				.isEqualTo(product);
		}

		@DisplayName("이름은 비속어를 사용할 수 없다.")
		@Test
		void createFailByProfanity() {
			// given
			when(purgomalumClient.containsProfanity(any()))
				.thenReturn(true);

			// when, then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> productService.create(product));
		}

		@DisplayName("가격은 빈 값이 될 수 없으며 `0원 이상`이어야 한다.")
		@Test
		void createFailByPrice() {
			// given
			BigDecimal price = BigDecimal.valueOf(-1);

			// when
			Product negativePriceProduct = ProductStub.createCustom("상품", price);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> productService.create(negativePriceProduct));
		}

		@DisplayName("상품의 가격은 일정 수준의 오차도 허용해서는 안 된다.")
		@ParameterizedTest(name = "상품의 가격은 {0}이어야 한다.")
		@ValueSource(doubles = {
			1.1, 1.01, 1.001, 1.0001, 1.00001, 1.000001, 1.0000001, 1.00000001,
			1.000000001, 1.0000000001, 1.00000000001, 1.000000000001, 1.0000000000001,
			1.00000000000001, 1.000000000000001, 1.0000000000000001, 1.00000000000000001,
			1.000000000000000001, 1.0000000000000000001, 1.00000000000000000001, 1.000000000000000000001
		})
		void createWithPrecisePrice(double priceValue) {
			// given
			BigDecimal price = BigDecimal.valueOf(priceValue);

			// when
			Product negativePriceProduct = ProductStub.createCustom("상품", price);

			// then
			assertThat(negativePriceProduct.getPrice())
				.isCloseTo(price, Percentage.withPercentage(100));
		}
	}

	@DisplayName("상품 조회 시")
	@Nested
	class FindTest {

		@DisplayName("전체를 조회할 수 있다.")
		@Test
		void findAll() {
			// given
			List<Product> allProducts = List.of(product, product);

			when(productRepository.findAll())
				.thenReturn(allProducts);

			// when
			List<Product> results = productService.findAll();

			// then
			assertAll(
				() -> assertThat(results).hasSize(allProducts.size()),
				() -> assertThat(results).containsExactlyElementsOf(allProducts)
			);
		}
	}

	@DisplayName("상품 변경 시")
	@Nested
	class ChangeTest {

		@DisplayName("가격을 변경할 수 있다.")
		@Test
		void changePrice() {
			// given
			List<Menu> createdProductRelatedMenus = List.of(MenuStub.createDefault());

			when(productRepository.findById(any()))
				.thenReturn(Optional.of(product));

			when(menuRepository.findAllByProductId(any()))
				.thenReturn(createdProductRelatedMenus);

			// when
			Product result = productService.changePrice(product.getId(), newProduct);

			// then
			assertThat(result.getPrice())
				.isEqualTo(newProduct.getPrice());
		}

		@DisplayName("가격은 빈 값이 될 수 없으며 `0원 이상`이어야 한다.")
		@Test
		void changePriceFailByNegativePrice() {
			// given
			Product newProduct = ProductStub.createCustom("상품", BigDecimal.valueOf(-1));

			// when, then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> productService.changePrice(product.getId(), newProduct));
		}

		@DisplayName("등록되지 않은 상품의 가격은 변경할 수 없다.")
		@Test
		void canNotChangePriceOfNonExistsProduct() {
			// given
			when(productRepository.findById(any()))
				.thenReturn(Optional.empty());

			// when, then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> productService.changePrice(product.getId(), newProduct));
		}

		@DisplayName("메뉴 가격이 해당 상품 가격의 합보다 크면 메뉴를 진열하지 않는다.")
		@Test
		void hideMenu() {
			// given
			Menu menu = MenuStub.createCustom(
				"메뉴",
				BigDecimal.valueOf(5000),
				true,
				MenuGroupStub.createDefault(),
				MenuProductStub.createDefaultList()
			);

			when(productRepository.findById(any()))
				.thenReturn(Optional.of(product));

			when(menuRepository.findAllByProductId(any()))
				.thenReturn(List.of(menu));

			// when
			productService.changePrice(product.getId(), product);

			// then
			assertThat(menu.isDisplayed())
				.isFalse();
		}
	}
}
