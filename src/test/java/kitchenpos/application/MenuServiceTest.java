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

	private UUID validMenuGroupId;

	private MenuGroup validMenuGroup;

	private UUID validProductId;

	private Product validProduct;

	private MenuProduct validMenuProduct;

	private UUID validMenuId;

	private Menu validMenu;

	@BeforeEach
	void setUp() {
		validMenuGroupId = UUID.randomUUID();
		validMenuGroup = new MenuGroup();
		validMenuGroup.setId(validMenuGroupId);
		validMenuGroup.setName("점심 특선");

		validProductId = UUID.randomUUID();
		validProduct = new Product();
		validProduct.setId(validProductId);
		validProduct.setName("치즈 버거");
		validProduct.setPrice(new BigDecimal("8.99"));

		validMenuProduct = new MenuProduct();
		validMenuProduct.setProduct(validProduct);
		validMenuProduct.setQuantity(2);
		validMenuProduct.setProductId(validProductId);

		validMenuId = UUID.randomUUID();
		validMenu = new Menu();
		validMenu.setId(validMenuId);
		validMenu.setName("버거 세트");
		validMenu.setPrice(new BigDecimal("17.98"));
		validMenu.setMenuGroup(validMenuGroup);
		validMenu.setMenuGroupId(validMenuGroupId);
		validMenu.setMenuProducts(List.of(validMenuProduct));
		validMenu.setDisplayed(true);

		lenient().when(menuGroupRepository.findById(validMenuGroupId)).thenReturn(Optional.of(validMenuGroup));
		lenient().when(productRepository.findAllByIdIn(List.of(validProductId))).thenReturn(List.of(validProduct));
		lenient().when(productRepository.findById(validProductId)).thenReturn(Optional.of(validProduct));

		lenient().when(purgomalumClient.containsProfanity(anyString())).thenAnswer(invocation -> {
			String argument = invocation.getArgument(0);
			return "비속어".equals(argument);
		});
	}

	@Nested
	class create {
		@ParameterizedTest
		@NullSource
		@ValueSource(strings = {"-1"})
		@DisplayName("메뉴 생성 시 가격이 null이거나 0미만이면 IllegalArgumentException 발생")
		void testCreateMenuWithInvalidPrice(BigDecimal price) {
			// given
			validMenu.setPrice(price);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(validMenu);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 메뉴 그룹 ID가 존재하지 않으면 NoSuchElementException 발생")
		void testCreateMenuWithNonexistentMenuGroup() {
			// given
			when(menuGroupRepository.findById(any())).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(validMenu);
				});
		}

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("메뉴 생성 시 메뉴 상품 목록이 null이거나 비어있으면 IllegalArgumentException 발생")
		void testCreateMenuWithNoMenuProducts(List<MenuProduct> menuProducts) {
			// given
			validMenu.setMenuProducts(menuProducts);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(validMenu);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 제공된 상품 ID의 수와 상품 목록의 수가 일치하지 않으면 IllegalArgumentException 발생")
		void testCreateMenuWithMismatchedProducts() {
			// given
			when(productRepository.findAllByIdIn(List.of(validProductId))).thenReturn(
				List.of(validProduct, new Product()));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(validMenu);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 상품의 수량이 음수인 경우 IllegalArgumentException 발생")
		void testCreateMenuWithNegativeQuantity() {
			// given
			validMenuProduct.setQuantity(-1);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(validMenu);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 상품 ID로 상품을 조회하지 못하는 경우 IllegalArgumentException 발생")
		void testCreateMenuWithInvalidProductId() {
			// given
			when(productRepository.findById(validProductId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(validMenu);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 메뉴 가격이 상품 가격 총합보다 클 때 IllegalArgumentException 발생")
		void testCreateMenuWithExcessivePrice() {
			// given
			BigDecimal excessivePrice = validProduct.getPrice().multiply(new BigDecimal("10"));
			validMenu.setPrice(excessivePrice);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(validMenu);
				});
		}

		@ParameterizedTest
		@MethodSource("provideInvalidProductNamesForMenuCreation")
		@DisplayName("메뉴 생성 시 이름 null 이거나 비속어가 포함되어 있으면 IllegalArgumentException 발생")
		void testCreateMenuWithInvalidProductNames(BigDecimal price, String name) {
			// given
			validMenu.setPrice(price);
			validMenu.setName(name);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(validMenu);
				});
		}

		private static Stream<Arguments> provideInvalidProductNamesForMenuCreation() {
			return Stream.of(
				Arguments.of(new BigDecimal("6.99"), null),
				Arguments.of(new BigDecimal("6.99"), "비속어"),
				Arguments.of(new BigDecimal("8.99"), null),
				Arguments.of(new BigDecimal("8.99"), "비속어")
			);
		}

		@ParameterizedTest
		@MethodSource("provideValidProductPricesAndNamesForMenuCreation")
		@DisplayName("메뉴 생성 성공 케이스")
		void testCreateMenuWithValidProductPricesAndNames(BigDecimal price, String name) {
			// given
			validMenu.setPrice(price);
			validMenu.setName(name);

			when(menuRepository.save(any(Menu.class))).thenReturn(validMenu);

			// when
			Menu result = menuService.create(validMenu);

			// then
			assertThat(result).isNotNull();
		}

		private static Stream<Arguments> provideValidProductPricesAndNamesForMenuCreation() {
			return Stream.of(
				Arguments.of(new BigDecimal("6.99"), "점심특선"),
				Arguments.of(new BigDecimal("8.99"), "여름한정메뉴")
			);
		}
	}

	@Nested
	class changePrice {
		@ParameterizedTest
		@NullSource
		@ValueSource(strings = {"-1", "-100.0"})
		@DisplayName("메뉴 가격 변경 시 가격이 null이거나 0미만인 경우 IllegalArgumentException 발생")
		void testChangePriceWithInvalidPrice(BigDecimal price) {
			// given
			lenient().when(menuRepository.findById(validMenuId)).thenReturn(Optional.of(validMenu));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.changePrice(validMenuId, buildRequestMenu(price));
				});
		}

		@Test
		@DisplayName("메뉴 가격 변경 시 해당 메뉴 ID가 존재하지 않을 경우 NoSuchElementException 발생")
		void testChangePriceWithNonexistentMenu() {
			// given
			UUID nonexistentMenuId = UUID.randomUUID();
			when(menuRepository.findById(nonexistentMenuId)).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					menuService.changePrice(nonexistentMenuId, buildRequestMenu(new BigDecimal("10.00")));
				});
		}

		@ParameterizedTest
		@ValueSource(strings = {"17.99", "100.00"}) // Assuming validMenu's sum of product prices is 17.99
		@DisplayName("메뉴 가격 변경 시 변경 가격이 상품 가격 총합보다 클 때 IllegalArgumentException 발생")
		void testChangePriceWithExcessivePrice(BigDecimal price) {
			// given
			when(menuRepository.findById(validMenuId)).thenReturn(Optional.of(validMenu));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.changePrice(validMenuId, buildRequestMenu(price));
				});
		}

		@ParameterizedTest
		@ValueSource(strings = {"15.00", "17.98"})
		@DisplayName("메뉴 가격 변경 시 변경 가격이 상품 가격 총합보다 작거나 같을 때 메뉴 가격이 성공적으로 변경됨")
		void testChangePriceWithValidPrice(BigDecimal price) {
			// given
			Menu requestMenu = buildRequestMenu(price);

			when(menuRepository.findById(validMenuId)).thenReturn(Optional.of(validMenu));
			lenient().when(menuRepository.save(requestMenu)).thenAnswer(invocation -> invocation.getArgument(0));

			// when
			Menu updatedMenu = menuService.changePrice(validMenuId, requestMenu);

			// then
			assertThat(updatedMenu.getPrice()).isEqualTo(price);
		}

		private Menu buildRequestMenu(BigDecimal price) {
			Menu requestMenu = new Menu();
			requestMenu.setId(validMenuId);
			requestMenu.setName(validMenu.getName());
			requestMenu.setPrice(price);
			requestMenu.setMenuGroup(validMenuGroup);
			requestMenu.setMenuGroupId(validMenuGroupId);
			requestMenu.setMenuProducts(List.of(validMenuProduct));
			requestMenu.setDisplayed(validMenu.isDisplayed());
			return requestMenu;
		}
	}

	@Nested
	class display {
		@Test
		@DisplayName("메뉴 노출 시 메뉴가 조회되지 않을 경우 NoSuchElementException 발생")
		void testDisplayMenuWithNonexistentMenu() {
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
		@DisplayName("메뉴 노출 시 메뉴 가격이 상품 가격 총합보다 크면 IllegalStateException 발생")
		void testDisplayMenuWithExcessivePrice(BigDecimal price) {
			// given
			validMenu.setPrice(price);
			when(menuRepository.findById(validMenuId)).thenReturn(Optional.of(validMenu));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					menuService.display(validMenuId);
				});
		}

		@ParameterizedTest
		@ValueSource(strings = {"12.34", "17.98"})
		@DisplayName("메뉴 노출 시 메뉴 가격이 상품 가격 총합과 같거나 작으면 메뉴가 성공적으로 표시됨")
		void testDisplayMenuSuccessfully(BigDecimal price) {
			// given
			validMenu.setPrice(price);
			when(menuRepository.findById(validMenuId)).thenReturn(Optional.of(validMenu));
			lenient().when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

			// when
			Menu displayedMenu = menuService.display(validMenuId);

			// then
			assertThat(displayedMenu.isDisplayed()).isTrue();
		}
	}

	@Nested
	class hide {
		@Test
		@DisplayName("메뉴 숨기기 시 메뉴가 조회되지 않을 경우 NoSuchElementException 발생")
		void testHideMenuWithNonexistentMenu() {
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
		@DisplayName("메뉴 숨기기 시 메뉴가 성공적으로 숨겨짐")
		void testHideMenuSuccessfully() {
			// given
			when(menuRepository.findById(validMenuId)).thenReturn(Optional.of(validMenu));
			lenient().when(menuRepository.save(validMenu)).thenAnswer(invocation -> invocation.getArgument(0));

			// when
			Menu hiddenMenu = menuService.hide(validMenuId);

			// then
			assertThat(hiddenMenu.isDisplayed()).isFalse();
		}
	}

	@Nested
	class findAll {
		@Test
		@DisplayName("메뉴 데이터가 저장되어 있지 않은 경우 모든 메뉴 조회 시 조회가 불가능하다")
		void findAllMenusWhenNotEmpty() {
			// given
			when(menuRepository.findAll()).thenReturn(Collections.emptyList());

			// when
			List<Menu> menus = menuService.findAll();

			// then
			assertThat(menus).isEmpty();
		}

		@Test
		@DisplayName("메뉴 데이터가 저장되어 있는 경우 모든 메뉴 조회 시 조회가 가능하다")
		void findAllMenusWhenEmpty() {
			// given
			when(menuRepository.findAll()).thenReturn(List.of(validMenu));

			// when
			List<Menu> menus = menuService.findAll();

			// then
			assertThat(menus).containsExactly(validMenu);
		}
	}
}

