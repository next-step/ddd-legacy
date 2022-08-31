package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import kitchenpos.common.MockitoUnitTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.stub.MenuGroupStub;
import kitchenpos.stub.MenuProductStub;
import kitchenpos.stub.MenuStub;
import kitchenpos.stub.ProductStub;

class MenuServiceTest extends MockitoUnitTest {

	@Mock
	private MenuRepository menuRepository;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private MenuGroupRepository menuGroupRepository;
	@Mock
	private PurgomalumClient purgomalumClient;

	@InjectMocks
	private MenuService menuService;

	private Menu menu;
	private Menu newMenu;
	private Product product;
	private Product newProduct;

	@BeforeEach
	void setUp() {
		menu = MenuStub.createDefault();
		newMenu = MenuStub.createCustom("newMenu", BigDecimal.valueOf(1000), true, MenuGroupStub.createDefault(), List.of(MenuProductStub.createDefault()));
		product = ProductStub.createDefault();
		newProduct = ProductStub.createCustom("신상품", BigDecimal.valueOf(2000));
	}

	@DisplayName("메뉴 생성 시")
	@Nested
	class CreateTest {

		@DisplayName("새로운 메뉴를 등록할 수 있다.")
		@Test
		void create() {
			// given
			when(menuGroupRepository.findById(menu.getMenuGroupId()))
				.thenReturn(Optional.of(menu.getMenuGroup()));
			when(productRepository.findAllByIdIn(any()))
				.thenReturn(List.of(product, newProduct));
			when(productRepository.findById(any()))
				.thenReturn(Optional.of(product));
			when(menuRepository.save(any()))
				.thenReturn(menu);

			// when
			Menu result = menuService.create(menu);

			// then
			assertThat(result).isNotNull();
		}

		@DisplayName("가격은 빈 값이 될 수 없으며 `0원 이상`이어야 한다.")
		@ParameterizedTest
		@ValueSource(longs = { -1, -5000 })
		void createFailByInvalidPrice(long price) {
			// given
			Menu request = new Menu();
			request.setPrice(BigDecimal.valueOf(price));

			// when
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> menuService.create(request));
		}

		@DisplayName("메뉴 그룹이 존재하지 않는다면 예외 처리한다.")
		@Test
		void createFailByInvalidMenuGroup() {
			// given
			when(menuGroupRepository.findById(any()))
				.thenReturn(Optional.empty());

			// when
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> menuService.create(menu));
		}

		@DisplayName("메뉴에 상품을 넣지 않는다면 예외 처리한다.")
		@Test
		void createFailByEmptyMenuProducts() {
			// given
			MenuGroup menuGroup = MenuGroupStub.createDefault();
			Menu menu = MenuStub.createCustom(
				"메뉴",
				BigDecimal.valueOf(10000),
				true,
				menuGroup,
				Collections.emptyList()
			);

			when(menuGroupRepository.findById(menu.getMenuGroupId()))
				.thenReturn(Optional.of(menuGroup));

			// when
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> menuService.create(menu));
		}

		@DisplayName("메뉴에 포함된 상품들이 모두 등록되어 있지 않다면 예외 처리한다.")
		@Test
		void createFailByNonRegisteredProduct() {
			// given
			when(menuGroupRepository.findById(menu.getMenuGroupId()))
				.thenReturn(Optional.of(MenuGroupStub.createDefault()));

			when(productRepository.findAllByIdIn(any()))
				.thenReturn(Collections.emptyList());

			// when
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> menuService.create(menu));
		}

		@DisplayName("메뉴에 포함된 상품들의 수량이 0 이하라면 예외 처리한다.")
		@Test
		void createFailByZeroQuantity() {
			// given
			Menu menu = MenuStub.createDefault();
			menu.setMenuProducts(List.of(
				MenuProductStub.createCustom(product, -1)
			));

			// when
			when(menuGroupRepository.findById(menu.getMenuGroupId()))
				.thenReturn(Optional.of(MenuGroupStub.createDefault()));
			when(productRepository.findAllByIdIn(any()))
				.thenReturn(Collections.singletonList(new Product()));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> menuService.create(menu));
		}

		@DisplayName("메뉴에 포함된 상품들의 수량이 메뉴에 포함된 상품들의 수량의 합보다 크다면 예외 처리한다.")
		@Test
		void createFailByPriceBiggerThanSum() {
			// given
			Menu menu = MenuStub.createCustom(
				"메뉴",
				BigDecimal.valueOf(1000),
				true,
				MenuGroupStub.createDefault(),
				List.of(
					MenuProductStub.createCustom(product, 5),
					MenuProductStub.createCustom(product, 5),
					MenuProductStub.createCustom(product, 5)
				)
			);

			when(menuGroupRepository.findById(menu.getMenuGroupId()))
				.thenReturn(Optional.of(menu.getMenuGroup()));

			when(productRepository.findAllByIdIn(any()))
				.thenReturn(List.of(product, newProduct));

			// when
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> menuService.create(menu));
		}

		@DisplayName("메뉴 이름은 빈 값이 될 수 없다.")
		@ParameterizedTest
		@NullAndEmptySource
		void createFailByEmptyName(String name) {
			// given
			Menu menu = MenuStub.createCustom(
				name, BigDecimal.valueOf(30000), true, MenuGroupStub.createDefault(), MenuProductStub.createDefaultList());

			when(menuGroupRepository.findById(menu.getMenuGroupId()))
				.thenReturn(Optional.of(MenuGroupStub.createDefault()));

			when(productRepository.findAllByIdIn(any()))
				.thenReturn(Arrays.asList(product, newProduct));

			when(productRepository.findById(any()))
				.thenReturn(Optional.of(product));

			// when
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> menuService.create(menu));
		}

		@DisplayName("메뉴 이름에 비속어를 사용할 수 없다.")
		@Test
		void createFailByProfanity() {
			// given
			when(menuGroupRepository.findById(menu.getMenuGroupId()))
				.thenReturn(Optional.of(menu.getMenuGroup()));

			when(productRepository.findAllByIdIn(any()))
				.thenReturn(List.of(product, newProduct));

			when(productRepository.findById(any()))
				.thenReturn(Optional.of(product));

			when(purgomalumClient.containsProfanity(anyString()))
				.thenReturn(true);

			// when
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> menuService.create(menu));
		}
	}

	@DisplayName("메뉴 변경 시")
	@Nested
	class UpdateTest {

		@DisplayName("가격을 변경할 수 있다.")
		@Test
		void changePrice() {
			// given
			when(menuRepository.findById(any()))
				.thenReturn(Optional.of(menu));

			// when
			Menu result = menuService.changePrice(menu.getId(), newMenu);

			// then
			assertThat(result.getPrice())
				.isEqualTo(newMenu.getPrice());
		}

		@DisplayName("가격은 0원 이상이어야 한다.")
		@ParameterizedTest
		@ValueSource(longs = { -1, -500 })
		void changePriceFailByInvalidPrice(long price) {
			// given
			Menu menu = MenuStub.createCustom(
				"신메뉴",
				BigDecimal.valueOf(price),
				true,
				MenuGroupStub.createDefault(),
				MenuProductStub.createDefaultList()
			);

			// when
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> menuService.changePrice(menu.getId(), menu));
		}

		@DisplayName("가격 변경 시 입력한 가격이 상품 가격의 합보다 크면 예외 처리한다.")
		@Test
		void changePriceFailByPrice() {
			// given
			Menu expensiveMenu = MenuStub.createCustom(
				"비싼 메뉴",
				BigDecimal.valueOf(30000),
				true,
				MenuGroupStub.createDefault(),
				MenuProductStub.createDefaultList()
			);

			when(menuRepository.findById(any()))
				.thenReturn(Optional.of(expensiveMenu));

			// when
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> menuService.changePrice(menu.getId(), expensiveMenu));
		}

		@DisplayName("판매상태 변경 시 메뉴 가격이 메뉴에 포함된 상품들의 가격 합보다 크다면 예외 처리한다.")
		@Test
		void displayFailByInvalidPrice() {
			// given
			Menu menu = MenuStub.createCustom(
				"신메뉴",
				BigDecimal.valueOf(100000),
				true,
				MenuGroupStub.createDefault(),
				MenuProductStub.createDefaultList()
			);

			when(menuRepository.findById(any()))
				.thenReturn(Optional.of(menu));

			// when
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> menuService.display(menu.getId()));
		}

		@DisplayName("진열 상태로 변경할 수 있다.")
		@Test
		void display() {
			// given
			when(menuRepository.findById(menu.getId()))
				.thenReturn(Optional.of(menu));

			// when
			Menu result = menuService.display(menu.getId());

			// then
			assertThat(result.isDisplayed())
				.isTrue();
		}

		@DisplayName("미진열 상태로 변경할 수 있다.")
		@Test
		void hide() {
			// given
			when(menuRepository.findById(menu.getId()))
				.thenReturn(Optional.of(menu));

			// when
			Menu result = menuService.hide(menu.getId());

			// then
			assertThat(result.isDisplayed())
				.isFalse();
		}
	}
}
