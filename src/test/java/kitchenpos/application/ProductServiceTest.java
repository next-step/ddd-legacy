package kitchenpos.application;

import static kitchenpos.application.ServiceTestFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
	@Mock
	private ProductRepository productRepository;

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private PurgomalumClient purgomalumClient;

	@InjectMocks
	private ProductService productService;

	private Product VALID_PRODUCT;

	@BeforeEach
	void setUp() {
		VALID_PRODUCT = createValidProduct();
	}

	@Nested
	class create {
		@Test
		@DisplayName("상품 생성 시 가격이 null이면 상품 생성을 할 수 없다")
		void createProductWithNullPrice() {
			// given
			VALID_PRODUCT.setPrice(null);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
					// when
					productService.create(VALID_PRODUCT)
				);
		}

		@Test
		@DisplayName("상품 생성 시 가격이 0 미만이면 상품 생성을 할 수 없다")
		void createProductWithNegativePrice() {
			// given
			VALID_PRODUCT.setPrice(BigDecimal.valueOf(-1));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
					// when
					productService.create(VALID_PRODUCT)
				);
		}

		@Test
		@DisplayName("상품 생성 시 상품명이 null이면 상품 생성을 할 수 없다")
		void createProductWithNullName() {
			// given
			VALID_PRODUCT.setName(null);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
					// when
					productService.create(VALID_PRODUCT)
				);
		}

		@Test
		@DisplayName("상품 생성 시 상품명에 비속어가 포함되어 있으면 상품 생성을 할 수 없다")
		void createProductWithProfaneName() {
			// given
			when(purgomalumClient.containsProfanity("비속어")).thenReturn(true);
			VALID_PRODUCT.setName("비속어");

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
					// when
					productService.create(VALID_PRODUCT)
				);
		}

		@Test
		@DisplayName("상품 생성을 할 수 있다")
		void createProductSuccessfully() {
			// given
			when(productRepository.save(any(Product.class))).thenReturn(VALID_PRODUCT);

			// when
			Product created = productService.create(VALID_PRODUCT);

			// then
			assertThat(created).isNotNull();
			assertThat(created.getName()).isEqualTo(VALID_PRODUCT.getName());
			assertThat(created.getPrice()).isEqualTo(VALID_PRODUCT.getPrice());
		}
	}

	@Nested
	class changePrice {
		@Test
		@DisplayName("상품 가격 변경 시 가격이 null이면 상품 가격 변경을 할 수 없다")
		void changePriceWithNullPrice() {
			// given
			VALID_PRODUCT.setPrice(null);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
					// when
					productService.changePrice(UUID.randomUUID(), VALID_PRODUCT)
				);
		}

		@Test
		@DisplayName("상품 가격 변경 시 가격이 0 미만이면 상품 가격 변경을 할 수 없다")
		void changePriceWithNegativePrice() {
			// given
			VALID_PRODUCT.setPrice(BigDecimal.valueOf(-1));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
					// when
					productService.changePrice(UUID.randomUUID(), VALID_PRODUCT)
				);
		}

		@Test
		@DisplayName("상품 가격 변경 시 상품 조회가 실패하면 상품 가격 변경을 할 수 없다")
		void changePriceWithNonExistentProduct() {
			// given
			when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() ->
					// when
					productService.changePrice(UUID.randomUUID(), VALID_PRODUCT)
				);
		}

		@Test
		@DisplayName("상품 가격 변경 시 해당 상품이 메뉴에 등록되어 있지 않으면 상품의 가격을 변경할 수 있다")
		void changePriceWithoutMenus() {
			// given
			when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(VALID_PRODUCT));
			when(menuRepository.findAllByProductId(VALID_PRODUCT.getId())).thenReturn(Collections.emptyList());

			// when
			Product updated = productService.changePrice(VALID_PRODUCT.getId(), VALID_PRODUCT);

			// then
			assertThat(updated).isNotNull();
			assertThat(updated.getPrice()).isEqualTo(VALID_PRODUCT.getPrice());
		}

		@Test
		@DisplayName("상품 가격 변경 시 메뉴 가격이 상품 가격 합계보다 높을 때 해당 상품의 메뉴가 비노출 처리된다")
		void changePriceMenuPriceHigher() {
			// given
			when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(VALID_PRODUCT));
			Menu menu = createValidMenuWithPrice(BigDecimal.valueOf(20));
			when(menuRepository.findAllByProductId(any(UUID.class))).thenReturn(Collections.singletonList(menu));

			// when
			productService.changePrice(VALID_PRODUCT.getId(), VALID_PRODUCT);

			// then
			assertThat(menu.isDisplayed()).isFalse();
		}

		@Test
		@DisplayName("상품 가격 변경 시 메뉴 가격이 상품 가격 합계와 같을 때 해당 상품의 메뉴가 노출 처리된다")
		void changePriceMenuPriceEqual() {
			// given
			when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(VALID_PRODUCT));
			Menu menu = createValidMenuWithPrice(BigDecimal.TEN);
			when(menuRepository.findAllByProductId(any(UUID.class))).thenReturn(Collections.singletonList(menu));

			// when
			productService.changePrice(VALID_PRODUCT.getId(), VALID_PRODUCT);

			// then
			assertThat(menu.isDisplayed()).isTrue();
		}
	}

	@Nested
	class findAll {
		@Test
		@DisplayName("상품 데이터가 비어 있을 때 모든 상품을 조회하면 모든 상품을 조회할 수 없다")
		void findAllProductsWhenEmpty() {
			// given
			when(productRepository.findAll()).thenReturn(Collections.emptyList());

			// when
			var products = productService.findAll();

			// then
			assertThat(products).isEmpty();
		}

		@Test
		@DisplayName("상품 데이터가 비어 있지 않을 때 모든 상품을 조회하면 모든 상품을 조회할 수 있다")
		void findAllProductsWhenNotEmpty() {
			// given
			when(productRepository.findAll()).thenReturn(Collections.singletonList(VALID_PRODUCT));

			// when
			var products = productService.findAll();

			// then
			assertThat(products).isNotEmpty();
			assertThat(products.getFirst()).isEqualTo(VALID_PRODUCT);
		}
	}

}