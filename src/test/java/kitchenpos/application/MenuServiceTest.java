package kitchenpos.application;

import static fixture.MenuFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fixture.MenuFixture;
import kitchenpos.domain.Menu;
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

	@Nested
	class create {
		@ParameterizedTest
		@NullSource
		@ValueSource(strings = {"-1"})
		@DisplayName("메뉴 생성 시 가격이 null이거나 0미만이면 메뉴를 생성할 수 없다")
		void createMenuWithInvalidPrice(BigDecimal price) {
			// given
			Menu menuWithInvalidPrice = MenuFixture.createWithPrice(price);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(menuWithInvalidPrice);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 메뉴 그룹 ID가 존재하지 않으면 메뉴를 생성할 수 없다")
		void createMenuWithNonexistentMenuGroup() {
			// given
			Menu menuWithNonExsitentMenuGroup = MenuFixture.createValid();
			when(menuGroupRepository.findById(menuWithNonExsitentMenuGroup.getMenuGroupId()))
				.thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(menuWithNonExsitentMenuGroup);
				});
		}

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("메뉴 생성 시 메뉴 상품 목록이 null이거나 비어있으면 메뉴를 생성할 수 없다")
		void createMenuWithNoMenuProducts(List<MenuProduct> menuProducts) {
			// given
			Menu menuWithNullOrEmptyMenuProducts = MenuFixture.createValid();
			menuWithNullOrEmptyMenuProducts.setMenuProducts(menuProducts);

			when(menuGroupRepository.findById(menuWithNullOrEmptyMenuProducts.getMenuGroup().getId()))
				.thenReturn(Optional.of(menuWithNullOrEmptyMenuProducts.getMenuGroup()));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(menuWithNullOrEmptyMenuProducts);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 제공된 상품 ID의 수와 상품 목록의 수가 일치하지 않으면 메뉴를 생성할 수 없다")
		void createMenuWithMismatchedProducts() {
			// given
			Menu menuWithMismatchedProducts = MenuFixture.createValid();
			MenuProduct fistMenuProduct = menuWithMismatchedProducts.getMenuProducts().get(0);

			when(menuGroupRepository.findById(menuWithMismatchedProducts.getMenuGroup().getId()))
				.thenReturn(Optional.of(menuWithMismatchedProducts.getMenuGroup()));

			when(productRepository.findAllByIdIn(List.of(fistMenuProduct.getProductId()))).thenReturn(
				List.of(fistMenuProduct.getProduct(), new Product()));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(menuWithMismatchedProducts);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 상품의 수량이 음수인 경우 메뉴를 생성할 수 없다")
		void createMenuWithNegativeQuantity() {
			// given
			Menu menuWithInvalidMenuProduct = MenuFixture.createValid();
			MenuProduct menuProductWithNegativeQuantity = menuWithInvalidMenuProduct.getMenuProducts().get(0);
			menuProductWithNegativeQuantity.setQuantity(-1);

			when(menuGroupRepository.findById(menuWithInvalidMenuProduct.getMenuGroup().getId()))
				.thenReturn(Optional.of(menuWithInvalidMenuProduct.getMenuGroup()));

			when(productRepository.findAllByIdIn(List.of(menuProductWithNegativeQuantity.getProductId())))
				.thenReturn(List.of(menuProductWithNegativeQuantity.getProduct()));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(menuWithInvalidMenuProduct);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 상품 ID로 상품을 조회하지 못하는 경우 메뉴를 생성할 수 없다")
		void createMenuWithInvalidProductId() {
			// given
			Menu menuWithInvalidProduct = MenuFixture.createValid();
			MenuProduct menuProductWithInvalidProductId = menuWithInvalidProduct.getMenuProducts().get(0);

			when(menuGroupRepository.findById(menuWithInvalidProduct.getMenuGroup().getId()))
				.thenReturn(Optional.of(menuWithInvalidProduct.getMenuGroup()));

			when(productRepository.findAllByIdIn(List.of(menuProductWithInvalidProductId.getProductId())))
				.thenReturn(List.of(menuProductWithInvalidProductId.getProduct()));

			when(productRepository.findById(menuProductWithInvalidProductId.getProductId()))
				.thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(menuWithInvalidProduct);
				});
		}

		@Test
		@DisplayName("메뉴 생성 시 메뉴 가격이 상품 가격 총합보다 클 때 메뉴를 생성할 수 없다")
		void createMenuWithExcessivePrice() {
			// given
			Menu menuWithExcessivePrice = MenuFixture.createValid();
			Product validProduct = menuWithExcessivePrice.getMenuProducts().get(0).getProduct();

			BigDecimal excessivePrice = validProduct.getPrice().multiply(new BigDecimal("10"));
			menuWithExcessivePrice.setPrice(excessivePrice);

			when(menuGroupRepository.findById(menuWithExcessivePrice.getMenuGroup().getId()))
				.thenReturn(Optional.of(menuWithExcessivePrice.getMenuGroup()));

			when(productRepository.findAllByIdIn(List.of(validProduct.getId())))
				.thenReturn(List.of(validProduct));

			when(productRepository.findById(validProduct.getId()))
				.thenReturn(Optional.of(validProduct));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(menuWithExcessivePrice);
				});
		}

		@ParameterizedTest
		@NullSource
		@ValueSource(strings = {MENU_NAME_WITH_PROFANITY})
		@DisplayName("메뉴 생성 시 메뉴 이름이 null 이거나 비속어가 포함되어 있으면 메뉴를 생성할 수 없다")
		void createMenuWithInvalidProductNames(String name) {
			// given
			Menu menuWithProfanityName = MenuFixture.createValid();
			menuWithProfanityName.setName(name);

			Product validProduct = menuWithProfanityName.getMenuProducts().get(0).getProduct();

			when(menuGroupRepository.findById(menuWithProfanityName.getMenuGroup().getId()))
				.thenReturn(Optional.of(menuWithProfanityName.getMenuGroup()));

			when(productRepository.findAllByIdIn(List.of(validProduct.getId())))
				.thenReturn(List.of(validProduct));

			when(productRepository.findById(validProduct.getId()))
				.thenReturn(Optional.of(validProduct));

			lenient().when(purgomalumClient.containsProfanity(MENU_NAME_WITH_PROFANITY)).thenReturn(true);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.create(menuWithProfanityName);
				});
		}

		@ParameterizedTest
		@ValueSource(strings = {"6.99", "8.99"})
		@DisplayName("메뉴를 정상적으로 생성할 수 있다")
		void createMenuWithValidProductPricesAndNames(BigDecimal price) {
			// given
			Menu validMenu = MenuFixture.createWithPrice(price);
			Product validProduct = validMenu.getMenuProducts().get(0).getProduct();

			when(menuGroupRepository.findById(validMenu.getMenuGroup().getId()))
				.thenReturn(Optional.of(validMenu.getMenuGroup()));

			when(productRepository.findAllByIdIn(List.of(validProduct.getId())))
				.thenReturn(List.of(validProduct));

			when(productRepository.findById(validProduct.getId()))
				.thenReturn(Optional.of(validProduct));

			lenient().when(purgomalumClient.containsProfanity(MENU_NAME_WITH_PROFANITY)).thenReturn(true);

			when(menuRepository.save(any(Menu.class))).thenReturn(validMenu);

			// when
			Menu result = menuService.create(validMenu);

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
			Menu menuWithInvalidPrice = MenuFixture.createWithPrice(price);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.changePrice(menuWithInvalidPrice.getId(), menuWithInvalidPrice);
				});
		}

		@Test
		@DisplayName("메뉴 가격 변경 시 해당 메뉴 ID가 존재하지 않을 경우 메뉴 가격을 변경할 수 없다")
		void changePriceWithNonexistentMenu() {
			// given
			Menu nonExistentMenu = MenuFixture.createValid();
			when(menuRepository.findById(nonExistentMenu.getId())).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					menuService.changePrice(
						nonExistentMenu.getId(),
						MenuFixture.createWithPrice(new BigDecimal("10.00"))
					);
				});
		}

		@ParameterizedTest
		@ValueSource(strings = {"17.99", "100.00"})
		@DisplayName("메뉴 가격 변경 시 변경 가격이 상품 가격 총합보다 클 때 메뉴 가격을 변경할 수 없다")
		void changePriceWithExcessivePrice(BigDecimal price) {
			// given
			Menu menuWithExcessivePrice = MenuFixture.createWithPrice(price);

			when(menuRepository.findById(menuWithExcessivePrice.getId()))
				.thenReturn(Optional.of(menuWithExcessivePrice));

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					menuService.changePrice(menuWithExcessivePrice.getId(), menuWithExcessivePrice);
				});
		}

		@ParameterizedTest
		@ValueSource(strings = {"15.00", "17.98"})
		@DisplayName("메뉴 가격 변경 시 변경 가격이 상품 가격 총합보다 작거나 같을 때 메뉴 가격을 변경할 수 있다")
		void changePriceWithValidPrice(BigDecimal price) {
			// given
			Menu menuWithValidPrice = MenuFixture.createWithPrice(price);

			when(menuRepository.findById(menuWithValidPrice.getId())).thenReturn(Optional.of(menuWithValidPrice));
			lenient().when(menuRepository.save(menuWithValidPrice)).thenReturn(menuWithValidPrice);

			// when
			Menu updatedMenu = menuService.changePrice(menuWithValidPrice.getId(), menuWithValidPrice);

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
			Menu nonExistentMenu = MenuFixture.createValid();
			when(menuRepository.findById(nonExistentMenu.getId())).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					menuService.display(nonExistentMenu.getId());
				});
		}

		@ParameterizedTest
		@ValueSource(strings = {"17.99", "18.00", "22.00"})
		@DisplayName("메뉴 노출 시 메뉴 가격이 상품 가격 총합보다 크면 메뉴 노출을 할 수 없다")
		void displayMenuWithExcessivePrice(BigDecimal price) {
			// given
			Menu menuWithExcessivePrice = MenuFixture.createWithPrice(price);
			when(menuRepository.findById(menuWithExcessivePrice.getId())).thenReturn(
				Optional.of(menuWithExcessivePrice));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					menuService.display(menuWithExcessivePrice.getId());
				});
		}

		@ParameterizedTest
		@ValueSource(strings = {"12.34", "17.98"})
		@DisplayName("메뉴 노출 시 메뉴 가격이 상품 가격 총합과 같거나 작으면 메뉴를 노출을 할 수 있다")
		void displayMenuSuccessfully(BigDecimal price) {
			// given
			Menu menuWithValidPrice = MenuFixture.createWithPrice(price);

			when(menuRepository.findById(menuWithValidPrice.getId())).thenReturn(Optional.of(menuWithValidPrice));
			lenient().when(menuRepository.save(menuWithValidPrice)).thenReturn(menuWithValidPrice);

			// when
			Menu displayedMenu = menuService.display(menuWithValidPrice.getId());

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
			Menu nonExistentMenu = MenuFixture.createValid();
			when(menuRepository.findById(nonExistentMenu.getId())).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					menuService.hide(nonExistentMenu.getId());
				});
		}

		@Test
		@DisplayName("메뉴 숨김 처리를 할 수 있다")
		void hideMenuSuccessfully() {
			// given
			Menu validMenu = MenuFixture.createValid();

			when(menuRepository.findById(validMenu.getId())).thenReturn(Optional.of(validMenu));
			lenient().when(menuRepository.save(validMenu)).thenReturn(validMenu);

			// when
			Menu hiddenMenu = menuService.hide(validMenu.getId());

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
			Menu validMenu = MenuFixture.createValid();
			Menu anotherValidMenu = MenuFixture.createWithNameAndPrice("모닝세트", BigDecimal.TEN);

			when(menuRepository.findAll()).thenReturn(List.of(validMenu, anotherValidMenu));

			// when
			List<Menu> menus = menuService.findAll();

			// then
			assertThat(menus).containsAll(List.of(validMenu, anotherValidMenu));
		}
	}
}

