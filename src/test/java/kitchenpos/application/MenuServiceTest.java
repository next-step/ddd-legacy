package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
	public static final String MENU_NAME = "menu name";
	public static final String MENU_GROUP_NAME = "menu group name";
	public static final String PRODUCT_NAME = "product name";
	private static final long ZERO = 0L;
	public static final long POSITIVE_NUM = 1L;
	private static final Long NEGATIVE_NUM = -1L;
	public static final BigDecimal MENU_PRICE = BigDecimal.valueOf(10000);
	public static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(5000);
	public static final UUID RANDOM_UUID = UUID.randomUUID();
	//	- [ ] 메뉴(menu)
//  - [ ] 도메인 정보
//    - [ ] 메뉴는 이름과 가집니다.
//    - [ ] 메뉴는 메뉴에 올라간 상품 정보를 포함합니다.
//    - [ ] 메뉴의 가격은 0원 이상입니다.
//		- [ ] 메뉴는 가격을 가지고, 가격은 메뉴에 올라간 모든 상품들의 합보다 크거나 같아야 합니다.
//    - [ ] 메뉴는 메뉴 그룹에 포함이 됩니다.
//    - [ ] 메뉴 숨김 처리 여부 기능이 존재합니다.
//		- [ ] 숨김 처리가 된 메뉴는 주문을 할 수 없습니다.

	@Mock
	MenuRepository menuRepository;
	@Mock
	MenuGroupRepository menuGroupRepository;
	@Mock
	ProductRepository productRepository;
	@Mock
	PurgomalumClient purgomalumClient;

	@InjectMocks
	private MenuService menuService;

	private static Stream<BigDecimal> menuPriceNullAndMinus() {
		return Stream.of(
			BigDecimal.valueOf(NEGATIVE_NUM),
			null
		);
	}

	@Test
	@Order(1)
	@DisplayName("가게 점주는 메뉴를 추가 할 수 있습니다.")
	void addMenu() {
		//given
		Menu menu = mock(Menu.class);
		MenuGroup menuGroup = mock(MenuGroup.class);
		MenuProduct menuProduct = mock(MenuProduct.class);
		Product product = mock(Product.class);

		when(menu.getPrice()).thenReturn(MENU_PRICE);
		when(menuGroupRepository.findById(any())).thenReturn(Optional.ofNullable(menuGroup));
		List<MenuProduct> menuProducts = Arrays.asList(menuProduct, menuProduct);
		when(menu.getMenuProducts()).thenReturn(menuProducts);
		List<Product> productList = Arrays.asList(product, product);
		when(productRepository.findAllByIdIn(any())).thenReturn(productList);
		when(menuProduct.getQuantity()).thenReturn(POSITIVE_NUM);
		when(productRepository.findById(any())).thenReturn(Optional.ofNullable(product));
		when(requireNonNull(product).getPrice()).thenReturn(PRODUCT_PRICE);
		when(menu.getName()).thenReturn(MENU_NAME);

		//then
		menuService.create(menu);
	}

	@Order(2)
	@ParameterizedTest
	@MethodSource("menuPriceNullAndMinus")
	@DisplayName("메뉴의 가격은 0원 이상이어야 합니다.")
	void price(BigDecimal price) {
		//given
		Menu menu = mock(Menu.class);

		//when
		when(menu.getPrice()).thenReturn(price);

		//then
		assertThatThrownBy(() -> menuService.create(menu))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Order(3)
	@Test
	@DisplayName("메뉴는 메뉴 그룹에 속해 있지 않으면 예외를 던집니다.")
	void menuInMenuGroup() {
		//given
		Menu menu = mock(Menu.class);

		when(menu.getPrice()).thenReturn(BigDecimal.ZERO);
		when(menu.getMenuGroupId()).thenReturn(RANDOM_UUID);
		//when
		when(menuGroupRepository.findById(any())).thenReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> menuService.create(menu))
			.isInstanceOf(NoSuchElementException.class);
	}

	@Order(4)
	@Test
	@DisplayName("메뉴에 올라간 상품과 메뉴에 올라간 상품의 정보를 이용해 가져온 상품의 사이즈는 같습니다.")
	void checkProductAndMenuProduct() {
		//given
		Menu menu = mock(Menu.class);
		MenuGroup menuGroup = mock(MenuGroup.class);
		MenuProduct menuProduct = mock(MenuProduct.class);
		Product product = mock(Product.class);

		when(menu.getPrice()).thenReturn(MENU_PRICE);
		when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
		List<MenuProduct> menuProducts = Arrays.asList(menuProduct, menuProduct);
		when(menu.getMenuProducts()).thenReturn(menuProducts);
		//when
		List<Product> productList = Arrays.asList(product, product, product);
		when(productRepository.findAllByIdIn(any())).thenReturn(productList);

		//then
		assertThatThrownBy(() -> {
			menuService.create(menu);
		}).isInstanceOf(IllegalArgumentException.class);


	}

	@Test
	@Order(5)
	@DisplayName("메뉴에 올라간 상품의 수량은 0개 이상이어야 합니다.")
	void checkQuantity() {
		//given
		Menu menu = mock(Menu.class);
		MenuGroup menuGroup = mock(MenuGroup.class);
		MenuProduct menuProduct = mock(MenuProduct.class);
		Product product = mock(Product.class);

		when(menu.getPrice()).thenReturn(MENU_PRICE);
		when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
		List<MenuProduct> menuProducts = Arrays.asList(menuProduct, menuProduct);
		when(menu.getMenuProducts()).thenReturn(menuProducts);
		List<Product> productList = Arrays.asList(product, product);
		when(productRepository.findAllByIdIn(any())).thenReturn(productList);

		//when
		when(menuProduct.getQuantity()).thenReturn(NEGATIVE_NUM);

		//then
		assertThatThrownBy(() -> menuService.create(menu))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@Order(6)
	@DisplayName("메뉴 가격은 메뉴의 상품들 가격의 합보다 비쌀 수 없습니다.")
	void comparePrice() {
		//given
		Menu menu = mock(Menu.class);
		MenuGroup menuGroup = mock(MenuGroup.class);
		MenuProduct menuProduct = mock(MenuProduct.class);
		Product product = mock(Product.class);

		when(menu.getPrice()).thenReturn(MENU_PRICE);
		when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
		List<MenuProduct> menuProducts = Arrays.asList(menuProduct, menuProduct);
		when(menu.getMenuProducts()).thenReturn(menuProducts);
		List<Product> productList = Arrays.asList(product, product);
		when(productRepository.findAllByIdIn(any())).thenReturn(productList);
		when(menuProduct.getQuantity()).thenReturn(POSITIVE_NUM);
		//when
		when(requireNonNull(product).getPrice()).thenReturn(BigDecimal.ZERO);
		when(productRepository.findById(any())).thenReturn(Optional.of(product));

		//then
		assertThatThrownBy(() -> menuService.create(menu))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@Order(7)
	@DisplayName("가게 점주는 가격을 변경할 수 있습니다.")
	void changePriceInMenu() {
		//given
		Menu request = mock(Menu.class);
		Menu menu = mock(Menu.class);
		MenuProduct menuProduct = mock(MenuProduct.class);

		when(request.getPrice()).thenReturn(MENU_PRICE);
		when(menuRepository.findById(any())).thenReturn(Optional.ofNullable(menu));
		List<MenuProduct> menuProductList = Arrays.asList(menuProduct, menuProduct);
		when(requireNonNull(menu).getMenuProducts()).thenReturn(menuProductList);
		when(menuProduct.getProduct()).thenReturn(mock(Product.class));
		when(menuProduct.getProduct().getPrice()).thenReturn(PRODUCT_PRICE);
		when(menuProduct.getQuantity()).thenReturn(POSITIVE_NUM);

		//then
		menuService.changePrice(RANDOM_UUID, request);
		verify(menu).setPrice(MENU_PRICE);
	}

	@Order(8)
	@ParameterizedTest
	@MethodSource("menuPriceNullAndMinus")
	@DisplayName("변경하려는 메뉴의 가격은 0원 이상입니다.")
	void changePriceIsPositiveNum(BigDecimal price) {
		//given
		Menu request = mock(Menu.class);
		Menu menu = mock(Menu.class);
		MenuProduct menuProduct = mock(MenuProduct.class);

		//when
		when(request.getPrice()).thenReturn(price);

		//then
		assertThatThrownBy(() -> menuService.changePrice(RANDOM_UUID, request))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@Order(9)
	@DisplayName("메뉴 가격은 메뉴의 상품들 가격의 합보다 비쌀 수 없습니다.")
	void compareChangePrice() {
		//given
		Menu request = mock(Menu.class);
		Menu menu = mock(Menu.class);
		MenuProduct menuProduct = mock(MenuProduct.class);

		when(request.getPrice()).thenReturn(MENU_PRICE);
		when(menuRepository.findById(any())).thenReturn(Optional.ofNullable(menu));
		List<MenuProduct> menuProductList = Arrays.asList(menuProduct, menuProduct);
		when(requireNonNull(menu).getMenuProducts()).thenReturn(menuProductList);
		when(menuProduct.getProduct()).thenReturn(mock(Product.class));

		//when
		when(menuProduct.getProduct().getPrice()).thenReturn(BigDecimal.ZERO);
		when(menuProduct.getQuantity()).thenReturn(POSITIVE_NUM);

		//then
		assertThatThrownBy(() -> menuService.changePrice(RANDOM_UUID, request))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@Order(10)
	@DisplayName("가게 점주는 메뉴가 숨김 처리를 해제할 수 있습니다.")
	void uncoverMenu() {
		//given
		Menu menu = mock(Menu.class);
		MenuProduct menuProduct = mock(MenuProduct.class);

		when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
		List<MenuProduct> menuProductList = Arrays.asList(menuProduct, menuProduct);
		when(menu.getMenuProducts()).thenReturn(menuProductList);
		when(menuProduct.getProduct()).thenReturn(mock(Product.class));
		when(menuProduct.getProduct().getPrice()).thenReturn(PRODUCT_PRICE);
		when(menuProduct.getQuantity()).thenReturn(POSITIVE_NUM);
		when(menu.getPrice()).thenReturn(MENU_PRICE);

		//then
		menuService.display(RANDOM_UUID);
		verify(menu).setDisplayed(true);
	}

	@Test
	@Order(11)
	@DisplayName("메뉴 가격은 메뉴의 상품들 가격의 합보다 비쌀 수 없습니다.")
	void name() {
		//given
		Menu menu = mock(Menu.class);
		MenuProduct menuProduct = mock(MenuProduct.class);

		when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
		List<MenuProduct> menuProductList = Arrays.asList(menuProduct, menuProduct);
		when(menu.getMenuProducts()).thenReturn(menuProductList);
		when(menuProduct.getProduct()).thenReturn(mock(Product.class));

		//when
		when(menuProduct.getProduct().getPrice()).thenReturn(BigDecimal.ZERO);
		when(menuProduct.getQuantity()).thenReturn(POSITIVE_NUM);
		when(menu.getPrice()).thenReturn(MENU_PRICE);

		//then
		assertThatThrownBy(() -> menuService.display(RANDOM_UUID))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	@Order(12)
	@DisplayName("점주는 메뉴를 숨김 처리할 수 있습니다.")
	void hideMenu() {
		//given
		Menu menu = mock(Menu.class);

		when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

		//then
		menuService.hide(RANDOM_UUID);
		verify(menu).setDisplayed(false);
	}

	@Test
	@Order(13)
	@DisplayName("가게 점주와 가게 손님은 메뉴를 전부 조회할 수 있습니다.")
	void findAll() {
		//given
		Menu menu = mock(Menu.class);

		when(menuRepository.findAll()).thenReturn(Arrays.asList(menu, menu));

		//then
		menuService.findAll();
		verify(menuRepository).findAll();
	}


}