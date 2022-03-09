package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
	private static final BigDecimal MENU_PRICE = BigDecimal.valueOf(10000L);
	private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(10000L);
	private static final BigDecimal CHANGE_PRICE = BigDecimal.valueOf(20000L);

	private static final String PRODUCT_NAME = "product name";
	public static final UUID RANDOM_UUID = UUID.randomUUID();
	public static final long POSITIVE_NUM = 1L;
	private static final long NEGATIVE_NUM = -1L;
	private static final long ZERO = 0L;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private PurgomalumClient purgomalumClient;

	@InjectMocks
	private ProductService productService;


	private static Stream<BigDecimal> menuPriceNullAndMinus() {
		return Stream.of(
			BigDecimal.valueOf(NEGATIVE_NUM),
			null
		);
	}

	@Test
	@DisplayName("상품을 생성합니다.")
	void createProduct() {
		//given
		Product request = mock(Product.class);
		when(request.getPrice()).thenReturn(PRODUCT_PRICE);
		when(request.getName()).thenReturn(PRODUCT_NAME);

		//then
		productService.create(request);
		verify(productRepository).save(any());
	}

	@ParameterizedTest
	@MethodSource("menuPriceNullAndMinus")
	@DisplayName("상품의 가격은 0원 이상입니다.")
	void checkProducePrice1(BigDecimal price) {
		//given
		Product request = mock(Product.class);
		when(request.getPrice()).thenReturn(price);

		//then
		assertThatThrownBy(() -> {
			productService.create(request);
		}).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void changeProductPrice() {
		//given
		Product request = mock(Product.class);
		Menu menu = mock(Menu.class);
		MenuProduct menuProduct = mock(MenuProduct.class);

		when(request.getPrice()).thenReturn(CHANGE_PRICE);
		when(menu.getMenuProducts()).thenReturn(Collections.singletonList(menuProduct));
		when(menu.getPrice()).thenReturn(MENU_PRICE);
		when(menuProduct.getProduct()).thenReturn(mock(Product.class));
		when(menuProduct.getProduct().getPrice()).thenReturn(PRODUCT_PRICE);
		when(menuProduct.getQuantity()).thenReturn(POSITIVE_NUM);
		when(productRepository.findById(any())).thenReturn(Optional.of(mock(Product.class)));
		when(menuRepository.findAllByProductId(any())).thenReturn(Collections.singletonList(menu));

		//then
		Product product = productService.changePrice(RANDOM_UUID, request);
	}


	@ParameterizedTest
	@MethodSource("menuPriceNullAndMinus")
	@DisplayName("상품의 가격은 0원 이상입니다.")
	void checkProducePrice2(BigDecimal price) {
		//given
		Product request = mock(Product.class);
		when(request.getPrice()).thenReturn(price);

		//then
		assertThatThrownBy(() -> {
			productService.changePrice(RANDOM_UUID, request);
		}).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("메뉴의 가격이 메뉴에 올라간 상품의 가격의 합보다 크면 메뉴가 숨김 처리가 됩니다.")
	void checkMenuChange() {
		//given
		Product request = mock(Product.class);
		Menu menu = mock(Menu.class);
		MenuProduct menuProduct = mock(MenuProduct.class);

		when(request.getPrice()).thenReturn(PRODUCT_PRICE);
		when(menu.getMenuProducts()).thenReturn(Collections.singletonList(menuProduct));
		when(menu.getPrice()).thenReturn(MENU_PRICE);
		//when
		when(menuProduct.getProduct()).thenReturn(mock(Product.class));
		when(menuProduct.getProduct().getPrice()).thenReturn(CHANGE_PRICE);
		when(menuProduct.getQuantity()).thenReturn(ZERO);

		when(productRepository.findById(any())).thenReturn(Optional.of(mock(Product.class)));
		when(menuRepository.findAllByProductId(any())).thenReturn(Collections.singletonList(menu));

		//then
		productService.changePrice(RANDOM_UUID, request);
		verify(menu).setDisplayed(false);
	}

	@Test
	@DisplayName("상품의 모든 정보를 가져옵니다.")
	void findAll() {
		//given
		when(productRepository.findAll()).thenReturn(Collections.singletonList(mock(Product.class)));

		//then
		productService.findAll();
		verify(productRepository).findAll();
	}
}