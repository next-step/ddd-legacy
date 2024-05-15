package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fixture.MenuFixture;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
	@Mock
	private MenuRepository menuRepository;

	@Mock
	private MenuGroupRepository menuGroupRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private PurgomalumClient purgomalumClient;

	@InjectMocks
	private MenuService menuService;

	private UUID VALID_MENU_ID;

	private Menu VALID_MENU;

	private MenuGroup VALID_MENU_GROUP;

	private MenuProduct VALID_MENU_PRODUCT;

	private UUID VALID_PRODUCT_ID;

	private Product VALID_PRODUCT;

	@BeforeEach
	void setUp() {
		VALID_MENU = MenuFixture.createValidMenu();
		VALID_MENU_ID = VALID_MENU.getId();
		VALID_MENU_GROUP = VALID_MENU.getMenuGroup();
		VALID_MENU_PRODUCT = VALID_MENU.getMenuProducts().get(0);
		VALID_PRODUCT_ID = VALID_MENU.getMenuProducts().get(0).getProductId();
		VALID_PRODUCT = VALID_MENU.getMenuProducts().get(0).getProduct();

		lenient().when(menuGroupRepository.findById(VALID_MENU_GROUP.getId()))
			.thenReturn(Optional.of(VALID_MENU_GROUP));
		lenient().when(productRepository.findAllByIdIn(List.of(VALID_PRODUCT_ID))).thenReturn(List.of(VALID_PRODUCT));
		lenient().when(productRepository.findById(VALID_PRODUCT.getId())).thenReturn(Optional.of(VALID_PRODUCT));

		lenient().when(purgomalumClient.containsProfanity(anyString())).thenAnswer(invocation -> {
			String argument = invocation.getArgument(0);
			return "비속어".equals(argument);
		});
	}

	@Nested
	class create {
		private static Stream<Arguments> provideInvalidProductNamesForMenuCreation() {
			return Stream.of(
				Arguments.of(new BigDecimal("6.99"), null),
				Arguments.of(new BigDecimal("6.99"), "비속어"),
				Arguments.of(new BigDecimal("8.99"), null),
				Arguments.of(new BigDecimal("8.99"), "비속어")
			);
		}

		@ParameterizedTest
		@NullSource
		@ValueSource(strings = {"-1"})
		@DisplayName("메뉴 생성 시 가격이 null이거나 0미만이면 메뉴를 생성할 수 없다")
		void createMenuWithInvalidPrice(BigDecimal price) {
			// given
			VALID_MENU.setPrice(price);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(VALID_MENU);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 메뉴 그룹 ID가 존재하지 않으면 메뉴를 생성할 수 없다")
		void createMenuWithNonexistentMenuGroup() {
			// given
			when(menuGroupRepository.findById(any())).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(VALID_MENU);
				});
		}

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("메뉴 생성 시 메뉴 상품 목록이 null이거나 비어있으면 메뉴를 생성할 수 없다")
		void createMenuWithNoMenuProducts(List<MenuProduct> menuProducts) {
			// given
			VALID_MENU.setMenuProducts(menuProducts);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(VALID_MENU);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 제공된 상품 ID의 수와 상품 목록의 수가 일치하지 않으면 메뉴를 생성할 수 없다")
		void createMenuWithMismatchedProducts() {
			// given
			when(productRepository.findAllByIdIn(List.of(VALID_PRODUCT_ID))).thenReturn(
				List.of(VALID_PRODUCT, new Product()));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(VALID_MENU);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 상품의 수량이 음수인 경우 메뉴를 생성할 수 없다")
		void createMenuWithNegativeQuantity() {
			// given
			VALID_MENU_PRODUCT.setQuantity(-1);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(VALID_MENU);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 상품 ID로 상품을 조회하지 못하는 경우 메뉴를 생성할 수 없다")
		void createMenuWithInvalidProductId() {
			// given
			when(productRepository.findById(VALID_PRODUCT_ID)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(VALID_MENU);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 메뉴 가격이 상품 가격 총합보다 클 때 메뉴를 생성할 수 없다")
		void createMenuWithExcessivePrice() {
			// given
			BigDecimal excessivePrice = VALID_PRODUCT.getPrice().multiply(new BigDecimal("10"));
			VALID_MENU.setPrice(excessivePrice);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(VALID_MENU);
				});
		}

		@ParameterizedTest
		@MethodSource("provideInvalidProductNamesForMenuCreation")
		@DisplayName("메뉴 생성 시 이름 null 이거나 비속어가 포함되어 있으면 메뉴를 생성할 수 없다")
		void createMenuWithInvalidProductNames(BigDecimal price, String name) {
			// given
			VALID_MENU.setPrice(price);
			VALID_MENU.setName(name);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(VALID_MENU);
				});
		}

		@ParameterizedTest
		@ValueSource(strings = {"6.99", "8.99"})
		@DisplayName("메뉴를 정상적으로 생성할 수 있다")
		void createMenuWithValidProductPricesAndNames(BigDecimal price) {
			// given
			VALID_MENU.setPrice(price);

			when(menuRepository.save(any(Menu.class))).thenReturn(VALID_MENU);

			// when
			Menu result = menuService.create(VALID_MENU);

			// then
			assertThat(result).isNotNull();
		}
	}

	@Nested
	class changePrice {
		@ParameterizedTest
		@NullSource
		@ValueSource(strings = {"-1", "-100.0"})
		@DisplayName("메뉴 가격 변경 시 가격이 null이거나 0미만인 경우 메뉴 가격을 변경할 수 없다")
		void changePriceWithInvalidPrice(BigDecimal price) {
			// given
			lenient().when(menuRepository.findById(VALID_MENU_ID)).thenReturn(Optional.of(VALID_MENU));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.changePrice(VALID_MENU_ID, MenuFixture.createValidMenuWithPrice(price));
				});
		}

		@Test
		@DisplayName("메뉴 가격 변경 시 해당 메뉴 ID가 존재하지 않을 경우 메뉴 가격을 변경할 수 없다")
		void changePriceWithNonexistentMenu() {
			// given
			UUID nonexistentMenuId = UUID.randomUUID();
			when(menuRepository.findById(nonexistentMenuId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					menuService.changePrice(nonexistentMenuId,
						MenuFixture.createValidMenuWithPrice(new BigDecimal("10.00")));
				});
		}

		@ParameterizedTest
		@ValueSource(strings = {"17.99", "100.00"})
		@DisplayName("메뉴 가격 변경 시 변경 가격이 상품 가격 총합보다 클 때 메뉴 가격을 변경할 수 없다")
		void changePriceWithExcessivePrice(BigDecimal price) {
			// given
			when(menuRepository.findById(VALID_MENU_ID)).thenReturn(Optional.of(VALID_MENU));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.changePrice(VALID_MENU_ID, MenuFixture.createValidMenuWithPrice(price));
				});
		}

		@ParameterizedTest
		@ValueSource(strings = {"15.00", "17.98"})
		@DisplayName("메뉴 가격 변경 시 변경 가격이 상품 가격 총합보다 작거나 같을 때 메뉴 가격을 변경할 수 있다")
		void changePriceWithValidPrice(BigDecimal price) {
			// given
			Menu requestMenu = MenuFixture.createValidMenuWithPrice(price);

			when(menuRepository.findById(VALID_MENU_ID)).thenReturn(Optional.of(VALID_MENU));
			lenient().when(menuRepository.save(requestMenu)).thenAnswer(invocation -> invocation.getArgument(0));

			// when
			Menu updatedMenu = menuService.changePrice(VALID_MENU_ID, requestMenu);

			// then
			assertThat(updatedMenu.getPrice()).isEqualTo(price);
		}
	}

	@Nested
	class display {
		@Test
		@DisplayName("메뉴 노출 시 메뉴가 조회되지 않을 경우 메뉴 노출을 할 수 없다")
		void displayMenuWithNonexistentMenu() {
			// given
			UUID nonexistentMenuId = UUID.randomUUID();
			when(menuRepository.findById(nonexistentMenuId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					menuService.display(nonexistentMenuId);
				});
		}

		@ParameterizedTest
		@ValueSource(strings = {"17.99", "18.00", "22.00"})
		@DisplayName("메뉴 노출 시 메뉴 가격이 상품 가격 총합보다 크면 메뉴 노출을 할 수 없다")
		void displayMenuWithExcessivePrice(BigDecimal price) {
			// given
			VALID_MENU.setPrice(price);
			when(menuRepository.findById(VALID_MENU_ID)).thenReturn(Optional.of(VALID_MENU));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					menuService.display(VALID_MENU_ID);
				});
		}

		@ParameterizedTest
		@ValueSource(strings = {"12.34", "17.98"})
		@DisplayName("메뉴 노출 시 메뉴 가격이 상품 가격 총합과 같거나 작으면 메뉴를 노출을 할 수 있다")
		void displayMenuSuccessfully(BigDecimal price) {
			// given
			VALID_MENU.setPrice(price);
			when(menuRepository.findById(VALID_MENU_ID)).thenReturn(Optional.of(VALID_MENU));
			lenient().when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

			// when
			Menu displayedMenu = menuService.display(VALID_MENU_ID);

			// then
			assertThat(displayedMenu.isDisplayed()).isTrue();
		}
	}

	@Nested
	class hide {
		@Test
		@DisplayName("메뉴 숨김 처리 시 메뉴가 조회되지 않을 경우 메뉴 숨김 처리를 할 수 없다")
		void hideMenuWithNonexistentMenu() {
			// given
			UUID nonexistentMenuId = UUID.randomUUID();
			when(menuRepository.findById(nonexistentMenuId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					menuService.hide(nonexistentMenuId);
				});
		}

		@Test
		@DisplayName("메뉴 숨김 처리를 할 수 있다")
		void hideMenuSuccessfully() {
			// given
			lenient().when(menuRepository.findById(VALID_MENU_ID)).thenReturn(Optional.of(VALID_MENU));
			lenient().when(menuRepository.save(VALID_MENU)).thenReturn(VALID_MENU);

			// when
			Menu hiddenMenu = menuService.hide(VALID_MENU_ID);

			// then
			assertThat(hiddenMenu.isDisplayed()).isFalse();
		}
	}

	@Nested
	class findAll {
		@Test
		@DisplayName("메뉴 데이터가 비어 있는 경우 모든 메뉴 조회를 할 수 없다")
		void findAllMenusWhenNotEmpty() {
			// given
			when(menuRepository.findAll()).thenReturn(Collections.emptyList());

			// when
			List<Menu> menus = menuService.findAll();

			// then
			assertThat(menus).isEmpty();
		}

		@Test
		@DisplayName("메뉴 데이터가 비어 있지 않는 경우 모든 메뉴 조회를 할 수 있다")
		void findAllMenusWhenEmpty() {
			// given
			when(menuRepository.findAll()).thenReturn(List.of(VALID_MENU));

			// when
			List<Menu> menus = menuService.findAll();

			// then
			assertThat(menus).containsExactly(VALID_MENU);
		}
	}
}

