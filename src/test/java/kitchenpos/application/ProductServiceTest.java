package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static kitchenpos.testBuilders.MenuBuilder.aDefaultMenu;
import static kitchenpos.testBuilders.ProductBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	ProductRepository productRepository;

	@Mock
	MenuRepository menuRepository;

	@Mock(lenient = true)
	PurgomalumClient purgomalumClient;

	@InjectMocks
	ProductService productService;

	@DisplayName("새로운 상품을 추가한다")
	@Test
	void createProduct() {
		// given
		Product request = aProduct()
				.withPrice(DEFAULT_PRODUCT_PRICE)
				.withName(DEFAULT_PRODUCT_NAME)
				.build();

		Product product = aDefaultProduct().build();

		given(productRepository.save(any())).willReturn(product);
		given(purgomalumClient.containsProfanity(any())).willReturn(false);

		// when
		Product result = productService.create(request);

		// then
		assertThat(result).isEqualTo(product);
	}

	@DisplayName("새로운 상품 추가 시 상품의 가격이 존재하지 않거나 0보다 작으면 예외가 발생한다")
	@ParameterizedTest(name = "상품의 가격: {0}")
	@NullSource
	@ValueSource(strings = {"-1"})
	void createInvalidPrice(BigDecimal price) {
		// given
		Product request = aProduct()
				.withPrice(price)
				.withName(DEFAULT_PRODUCT_NAME)
				.build();

		// when then
		assertThatThrownBy(() -> productService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("새로운 상품 추가 시 상품의 이름에 비속어가 포함된 경우 예외가 발생한다")
	@ParameterizedTest(name = "상품 이름: {0}")
	@NullSource
	@ValueSource(strings = {"욕욕욕"})
	void createInvalidName(String name) {
		// given
		Product request = aProduct()
				.withName(name)
				.withPrice(DEFAULT_PRODUCT_PRICE)
				.build();

		given(purgomalumClient.containsProfanity(name)).willReturn(true);

		// when then
		assertThatThrownBy(() -> productService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}


	@DisplayName("상품 가격 변경")
	@Test
	void changePrice() {
		// given
		UUID productId = UUID.randomUUID();
		BigDecimal productPrice = BigDecimal.valueOf(2000L);
		BigDecimal originProductPrice = BigDecimal.valueOf(3000L);
		BigDecimal menuPrice = BigDecimal.valueOf(2000L);

		Product request = aProduct()
				.withName(DEFAULT_PRODUCT_NAME)
				.withPrice(DEFAULT_PRODUCT_PRICE)
				.build();

		Product mockResultProduct = new Product();
		mockResultProduct.setId(productId);
		mockResultProduct.setPrice(originProductPrice);
		mockResultProduct.setName("짜장면");

		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setProductId(productId);
		menuProduct.setSeq(1L);
		menuProduct.setQuantity(1);
		menuProduct.setProduct(mockResultProduct);

		Menu menu = new Menu();
		menu.setDisplayed(true);
		menu.setName("짜장면");
		menu.setPrice(menuPrice);
		menu.setMenuProducts(Collections.singletonList(menuProduct));

		List<Menu> menus = Collections.singletonList(menu);

		given(productRepository.findById(productId)).willReturn(Optional.of(mockResultProduct));
		given(menuRepository.findAllByProductId(productId)).willReturn(menus);

		// when
		Product result = productService.changePrice(productId, request);

		// then
		assertThat(result).isNotNull();
	}


	@DisplayName("상품 가격 변경 시 가격이 존재하지 않거나 0원 미만인 경우 예외가 발생한다")
	@ParameterizedTest(name = "주문 가격: {0}")
	@NullSource
	@ValueSource(strings = {"-1"})
	void changeInvalidPrice(BigDecimal price) {
		// given
		UUID productId = UUID.randomUUID();

		Product request = aProduct()
				.withName(DEFAULT_PRODUCT_NAME)
				.withPrice(price)
				.build();

		// when then
		assertThatThrownBy(() -> productService.changePrice(productId, request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("상품 가격 변경 시 상품이 존재하지 않는 경우 예외가 발생한다")
	@Test
	void changePriceNotExist() {
		// given
		UUID notExistProductId = UUID.randomUUID();

		Optional<Product> emptyProduct = Optional.empty();

		Product request = aProduct()
				.withName(DEFAULT_PRODUCT_NAME)
				.withPrice(DEFAULT_PRODUCT_PRICE)
				.build();

		given(productRepository.findById(notExistProductId)).willReturn(emptyProduct);

		// when then
		assertThatThrownBy(() -> productService.changePrice(notExistProductId, request))
				.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("상품 가격 변경 시 해당 상품을 판매하는 메뉴의 가격이 상품가격보다 큰 경우 메뉴를 숨김처리한다")
	@ParameterizedTest(name = "기존 메뉴 전시 여부: {0}")
	@ValueSource(booleans = {true, false})
	void changePriceMenuDisplayed(boolean displayed) {
		// given
		Product request = aProduct()
				.withPrice(MORE_CHEAPER_PRODUCT_PRICE_THAN_DEFAULT_PRODUCT_PRICE)
				.withName(DEFAULT_PRODUCT_NAME)
				.build();

		Menu menu = aDefaultMenu().withDisplayed(displayed).build();
		Product product = menu.getMenuProducts().get(0).getProduct();
		List<Menu> menus = Collections.singletonList(menu);

		given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(product));
		given(menuRepository.findAllByProductId(menu.getMenuProducts().get(0).getProductId())).willReturn(menus);

		// when
		productService.changePrice(menu.getMenuProducts().get(0).getProductId(), request);

		// then
		assertThat(menu.isDisplayed()).isFalse();
	}

	@DisplayName("등록된 상품들을 조회한다")
	@Test
	void findAll() {
		// given
		Product product1 = aDefaultProduct().build();
		Product product2 = aDefaultProduct().build();

		List<Product> products = Arrays.asList(product1, product2);

		given(productRepository.findAll()).willReturn(products);

		// when
		List<Product> result = productService.findAll();

		// then
		assertThat(result).isSameAs(products);
	}
}
