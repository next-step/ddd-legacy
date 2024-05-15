package kitchenpos.application;

import static fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fixture.MenuFixture;
import fixture.ProductFixture;
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

	@Nested
	class create {
		@ParameterizedTest
		@NullSource
		@ValueSource(strings = {"-1"})
		@DisplayName("상품 생성 시 가격이 null이거나 0미만이면 상품 생성을 할 수 없다")
		void createProductWithNullPrice(BigDecimal price) {
			// given
			Product invalidPricedProduct = ProductFixture.create(VALID_PRODUCT_NAME, price);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
					// when
					productService.create(invalidPricedProduct)
				);
		}

		@Test
		@DisplayName("상품 생성 시 상품명이 null이면 상품 생성을 할 수 없다")
		void createProductWithNullName() {
			// given
			Product nullNamedProduct = ProductFixture.create(null, BigDecimal.TEN);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
					// when
					productService.create(nullNamedProduct)
				);
		}

		@Test
		@DisplayName("상품 생성 시 상품명에 비속어가 포함되어 있으면 상품 생성을 할 수 없다")
		void createProductWithProfaneName() {
			// given
			Product productWithProfanity = ProductFixture.create(PRODUCT_NAME_WITH_PROFANITY, BigDecimal.TEN);

			when(purgomalumClient.containsProfanity(PRODUCT_NAME_WITH_PROFANITY)).thenReturn(true);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
					// when
					productService.create(productWithProfanity)
				);
		}

		@Test
		@DisplayName("상품 생성을 할 수 있다")
		void createProductSuccessfully() {
			// given
			Product validProduct = ProductFixture.create(VALID_PRODUCT_NAME, VALID_PRODUCT_PRICE);
			when(productRepository.save(any(Product.class))).thenReturn(validProduct);

			// when
			Product createdProduct = productService.create(validProduct);

			// then
			assertThat(createdProduct).isNotNull();
			assertThat(createdProduct.getName()).isEqualTo(validProduct.getName());
			assertThat(createdProduct.getPrice()).isEqualTo(validProduct.getPrice());
		}
	}

	@Nested
	class changePrice {
		@ParameterizedTest
		@NullSource
		@ValueSource(strings = {"-1"})
		@DisplayName("상품 가격 변경 시 가격이 null이거나 0 미만이면 상품 가격 변경을 할 수 없다")
		void changePriceWithNullPrice(BigDecimal price) {
			// given
			Product invalidPricedProduct = ProductFixture.create(VALID_PRODUCT_NAME, price);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
					// when
					productService.changePrice(invalidPricedProduct.getId(), invalidPricedProduct)
				);
		}

		@Test
		@DisplayName("상품 가격 변경 시 상품 조회가 실패하면 상품 가격 변경을 할 수 없다")
		void changePriceWithNonExistentProduct() {
			// given
			Product nonExistentProduct = ProductFixture.create(VALID_PRODUCT_NAME, VALID_PRODUCT_PRICE);
			when(productRepository.findById(nonExistentProduct.getId())).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() ->
					// when
					productService.changePrice(nonExistentProduct.getId(), nonExistentProduct)
				);
		}

		@Test
		@DisplayName("상품 가격 변경 시 해당 상품이 메뉴에 등록되어 있지 않으면 상품의 가격을 변경할 수 있다")
		void changePriceWithoutMenus() {
			// given
			Product productNotRegisteredInMenu = ProductFixture.create(VALID_PRODUCT_NAME, VALID_PRODUCT_PRICE);

			when(productRepository.findById(productNotRegisteredInMenu.getId())).thenReturn(
				Optional.of(productNotRegisteredInMenu));
			when(menuRepository.findAllByProductId(productNotRegisteredInMenu.getId())).thenReturn(
				Collections.emptyList());

			// when
			Product updatedProduct = productService.changePrice(productNotRegisteredInMenu.getId(),
				productNotRegisteredInMenu);

			// then
			assertThat(updatedProduct).isNotNull();
			assertThat(updatedProduct.getPrice()).isEqualTo(productNotRegisteredInMenu.getPrice());
		}

		@Test
		@DisplayName("상품 가격 변경 시 상품들의 가격 합계가 메뉴가 가격보다 낮을 때 해당 상품의 메뉴가 비노출 처리된다")
		void changePriceMenuPriceHigher() {
			// given
			Product productWithPriceLowerThanMenuPrice = ProductFixture.create(VALID_PRODUCT_NAME, VALID_PRODUCT_PRICE);

			when(productRepository.findById(productWithPriceLowerThanMenuPrice.getId()))
				.thenReturn(Optional.of(productWithPriceLowerThanMenuPrice));
			Menu menu = MenuFixture.createWithPrice(VALID_PRODUCT_PRICE.add(BigDecimal.TEN));
			when(menuRepository.findAllByProductId(productWithPriceLowerThanMenuPrice.getId()))
				.thenReturn(Collections.singletonList(menu));

			// when
			productService.changePrice(productWithPriceLowerThanMenuPrice.getId(), productWithPriceLowerThanMenuPrice);

			// then
			assertThat(menu.isDisplayed()).isFalse();
		}

		@Test
		@DisplayName("상품 가격 변경 시 메뉴 가격이 상품 가격 합계와 같을 때 해당 상품의 메뉴가 노출 처리된다")
		void changePriceMenuPriceEqual() {
			// given
			Product validProduct = ProductFixture.create(VALID_PRODUCT_NAME, VALID_PRODUCT_PRICE);

			when(productRepository.findById(validProduct.getId())).thenReturn(Optional.of(validProduct));
			Menu menu = MenuFixture.createWithPrice(BigDecimal.TEN);
			when(menuRepository.findAllByProductId(validProduct.getId())).thenReturn(Collections.singletonList(menu));

			// when
			productService.changePrice(validProduct.getId(), validProduct);

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
			Product validProduct = ProductFixture.create(VALID_PRODUCT_NAME, VALID_PRODUCT_PRICE);
			Product anotherProduct = ProductFixture.create("불고기버거", BigDecimal.TEN);

			when(productRepository.findAll()).thenReturn(List.of(validProduct, anotherProduct));

			// when
			var products = productService.findAll();

			// then
			assertThat(products).isNotEmpty();
			assertThat(products.getFirst()).isEqualTo(validProduct);
			assertThat(products.getLast()).isEqualTo(anotherProduct);
		}
	}

}