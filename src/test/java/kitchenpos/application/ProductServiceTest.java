package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
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

	private Product product;

	@BeforeEach
	void setUp() {
		product = new Product();
		product.setId(UUID.randomUUID());
		product.setName("양념치킨");
		product.setPrice(BigDecimal.TEN);
	}

	@Test
	@DisplayName("상품 생성 시 가격이 null이면 IllegalArgumentException이 발생한다")
	void createProductWithNullPrice() {
		// given
		product.setPrice(null);

		// then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() ->
				// when
				productService.create(product)
			);
	}

	@Test
	@DisplayName("상품 생성 시 가격이 0 미만이면 IllegalArgumentException이 발생한다")
	void createProductWithNegativePrice() {
		// given
		product.setPrice(BigDecimal.valueOf(-1));

		// then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() ->
				// when
				productService.create(product)
			);
	}

	@Test
	@DisplayName("상품 생성 시 상품명이 null이면 IllegalArgumentException이 발생한다")
	void createProductWithNullName() {
		// given
		product.setName(null);

		// then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() ->
				// when
				productService.create(product)
			);
	}

	@Test
	@DisplayName("상품명에 비속어가 포함되어 있으면 생성 시 IllegalArgumentException이 발생한다")
	void createProductWithProfaneName() {
		// given
		when(purgomalumClient.containsProfanity("Profane")).thenReturn(true);
		product.setName("Profane");

		// then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() ->
				// when
				productService.create(product)
			);
	}

	@Test
	@DisplayName("상품이 정상적으로 등록되면 생성된 상품 정보를 확인할 수 있다")
	void createProductSuccessfully() {
		// given
		when(productRepository.save(any(Product.class))).thenReturn(product);

		// when
		Product created = productService.create(product);

		// then
		assertThat(created).isNotNull();
		assertThat(created.getName()).isEqualTo(product.getName());
		assertThat(created.getPrice()).isEqualTo(product.getPrice());
	}

	@Test
	@DisplayName("상품 가격 변경 시 가격이 null이면 IllegalArgumentException이 발생한다")
	void changePriceWithNullPrice() {
		// given
		product.setPrice(null);

		// then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() ->
				// when
				productService.changePrice(UUID.randomUUID(), product)
			);
	}

	@Test
	@DisplayName("상품 가격 변경 시 가격이 0 미만이면 IllegalArgumentException이 발생한다")
	void changePriceWithNegativePrice() {
		// given
		product.setPrice(BigDecimal.valueOf(-1));

		// then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() ->
				// when
				productService.changePrice(UUID.randomUUID(), product)
			);
	}

	@Test
	@DisplayName("상품 조회가 실패하면 NoSuchElementException이 발생한다")
	void changePriceWithNonExistentProduct() {
		// given
		when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

		// then
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() ->
				// when
				productService.changePrice(UUID.randomUUID(), product)
			);
	}

	@Test
	@DisplayName("메뉴에 해당 상품이 없으면 상품의 가격을 변경할 수 있다")
	void changePriceWithoutMenus() {
		// given
		when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
		when(menuRepository.findAllByProductId(any(UUID.class))).thenReturn(Collections.emptyList());

		// when
		Product updated = productService.changePrice(product.getId(), product);

		// then
		assertThat(updated).isNotNull();
		assertThat(updated.getPrice()).isEqualTo(BigDecimal.TEN);
	}

	@Test
	@DisplayName("메뉴 가격이 상품 가격 합계보다 높을 때 메뉴를 비표시로 변경한다")
	void changePriceMenuPriceHigher() {
		// given
		when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
		Menu menu = createTestMenu(product, BigDecimal.valueOf(20));
		when(menuRepository.findAllByProductId(any(UUID.class))).thenReturn(Collections.singletonList(menu));

		// when
		productService.changePrice(product.getId(), product);

		// then
		assertThat(menu.isDisplayed()).isFalse();
	}

	@Test
	@DisplayName("메뉴 가격이 상품 가격 합계와 같을 때 메뉴는 표시된다")
	void changePriceMenuPriceEqual() {
		// given
		when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
		Menu menu = createTestMenu(product, BigDecimal.TEN);
		when(menuRepository.findAllByProductId(any(UUID.class))).thenReturn(Collections.singletonList(menu));

		// when
		productService.changePrice(product.getId(), product);

		// then
		assertThat(menu.isDisplayed()).isTrue();
	}

	@Test
	@DisplayName("모든 상품을 조회하면 상품 목록이 비어있지 않다")
	void findAllProducts() {
		// given
		when(productRepository.findAll()).thenReturn(Collections.singletonList(product));

		// when
		var products = productService.findAll();

		// then
		assertThat(products).isNotEmpty();
	}

	private Menu createTestMenu(Product product, BigDecimal price) {
		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setProduct(product);
		menuProduct.setQuantity(1);
		
		Menu menu = new Menu();
		menu.setPrice(price);
		menu.setMenuProducts(List.of(menuProduct));
		menu.setDisplayed(true);
		return menu;
	}
}