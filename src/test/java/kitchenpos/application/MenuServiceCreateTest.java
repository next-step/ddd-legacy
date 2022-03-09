package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static kitchenpos.testBuilders.MenuBuilder.*;
import static kitchenpos.testBuilders.MenuGroupBuilder.aDefaultMenuGroup;
import static kitchenpos.testBuilders.ProductBuilder.aDefaultProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuServiceCreateTest {

	@Mock
	MenuRepository menuRepository;

	@Mock
	MenuGroupRepository menuGroupRepository;

	@Mock
	ProductRepository productRepository;

	@Mock(lenient = true)
	PurgomalumClient purgomalumClient;

	@InjectMocks
	MenuService menuService;

	@DisplayName("메뉴를 생성할 수 있다")
	@Test
	void create() {
		// given
		Menu request = new Menu();
		request.setName(DEFAULT_MENU_NAME);
		request.setPrice(DEFAULT_MENU_PRICE);
		request.setMenuProducts(DEFAULT_MENU_PRODUCTS);
		request.setDisplayed(DEFAULT_MENU_DISPLAYED);
		request.setMenuGroupId(UUID.randomUUID());

		MenuGroup menuGroup = aDefaultMenuGroup().withId(request.getMenuGroupId()).build();
		Product product = aDefaultProduct().withId(request.getMenuProducts().get(0).getProductId()).build();
		Menu menu = aDefaultMenu().build();

		UUID productId = product.getId();

		given(menuRepository.save(any(Menu.class))).willReturn(menu);
		given(menuGroupRepository.findById(request.getMenuGroupId())).willReturn(Optional.of(menuGroup));
		given(productRepository.findAllByIdIn(Collections.singletonList(productId))).willReturn(Collections.singletonList(product));
		given(productRepository.findById(productId)).willReturn(Optional.of(product));
		given(purgomalumClient.containsProfanity(DEFAULT_MENU_NAME)).willReturn(false);

		// when
		Menu result = menuService.create(request);

		// then
		assertThat(result).isSameAs(menu);
	}

	@DisplayName("메뉴 생성 시 메뉴 가격이 0보다 작은 경우 예외가 발생한다")
	@ParameterizedTest(name = "메뉴 가격: {0}")
	@NullSource
	@ValueSource(strings = "-1")
	void createInvalidPrice(BigDecimal price) {
		// given
		Menu request = aMenu()
				.withPrice(price)
				.build();

		// when then
		assertThatThrownBy(() -> menuService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴 생성 시 메뉴그룹이 존재하지 않는 경우 예외가 발생한다")
	@Test
	void createNotExistMenuGroup() {
		// given
		Menu request = new Menu();
		request.setPrice(BigDecimal.ONE);
		request.setMenuGroupId(UUID.randomUUID());

		given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> menuService.create(request))
				.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("메뉴 생성 시 메뉴상품이 비어있는 경우 예외가 발생한다")
	@ParameterizedTest(name = "메뉴상품: {0}")
	@NullAndEmptySource
	void createEmptyMenuProduct(List<MenuProduct> menuProducts) {
		// given
		Menu request = new Menu();
		request.setPrice(BigDecimal.ONE);
		request.setMenuGroupId(UUID.randomUUID());
		request.setMenuProducts(menuProducts);

		MenuGroup menuGroup = new MenuGroup();

		given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));

		// when then
		assertThatThrownBy(() -> menuService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴 생성 시 메뉴상품 중 존재하지 않는 메뉴상품이 하나라도 있는 경우 예외가 발생한다")
	@Test
	void createNotExistMenuProduct() {
		// given
		MenuProduct menuProduct1 = new MenuProduct();
		MenuProduct menuProduct2 = new MenuProduct();
		List<MenuProduct> menuProducts = new ArrayList<>();
		menuProducts.add(menuProduct1);
		menuProducts.add(menuProduct2);

		Product product = new Product();
		List<Product> products = new ArrayList<>();
		products.add(product);

		Menu request = new Menu();
		request.setPrice(BigDecimal.ONE);
		request.setMenuGroupId(UUID.randomUUID());
		request.setMenuProducts(menuProducts);

		MenuGroup menuGroup = new MenuGroup();

		given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
		given(productRepository.findAllByIdIn(any())).willReturn(products);

		// when then
		assertThatThrownBy(() -> menuService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴 생성 시 메뉴상품의 수량이 0보다 작은 경우 예외가 발생한다")
	@Test
	void createInvalidMenuProductQuantity() {
		// given
		long menuProductQuantity = -1;

		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setQuantity(menuProductQuantity);
		List<MenuProduct> menuProducts = new ArrayList<>();
		menuProducts.add(menuProduct);

		Product product = new Product();
		List<Product> products = new ArrayList<>();
		products.add(product);

		Menu request = new Menu();
		request.setPrice(BigDecimal.ONE);
		request.setMenuGroupId(UUID.randomUUID());
		request.setMenuProducts(menuProducts);

		MenuGroup menuGroup = new MenuGroup();

		given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
		given(productRepository.findAllByIdIn(any())).willReturn(products);

		// when then
		assertThatThrownBy(() -> menuService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴 생성 시 상품이 존재하지 않는 경우 예외가 발생한다")
	@Test
	void createNotExistProduct() {
		// given
		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setQuantity(1);
		List<MenuProduct> menuProducts = new ArrayList<>();
		menuProducts.add(menuProduct);

		Product product = new Product();
		List<Product> products = new ArrayList<>();
		products.add(product);

		Menu request = new Menu();
		request.setPrice(BigDecimal.ONE);
		request.setMenuGroupId(UUID.randomUUID());
		request.setMenuProducts(menuProducts);

		MenuGroup menuGroup = new MenuGroup();

		given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
		given(productRepository.findAllByIdIn(any())).willReturn(products);
		given(productRepository.findById(any())).willReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> menuService.create(request))
				.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("메뉴 생성 시 메뉴의 가격이 메뉴 상품들의 가격 합보다 큰 경우 예외가 발생한다")
	@Test
	void createInvalidSum() {
		// given
		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setQuantity(1);

		List<MenuProduct> menuProducts = new ArrayList<>();
		menuProducts.add(menuProduct);

		Product product = new Product();
		product.setPrice(BigDecimal.valueOf(3000));

		List<Product> products = new ArrayList<>();
		products.add(product);

		Menu request = new Menu();
		request.setPrice(BigDecimal.valueOf(2500));
		request.setMenuGroupId(UUID.randomUUID());
		request.setMenuProducts(menuProducts);

		MenuGroup menuGroup = new MenuGroup();

		given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
		given(productRepository.findAllByIdIn(any())).willReturn(products);
		given(productRepository.findById(any())).willReturn(Optional.of(product));

		// when then
		assertThatThrownBy(() -> menuService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴 생성 시 메뉴 이름에 비속어가 포함된 경우 예외가 발생한다")
	@ParameterizedTest(name = "메뉴 이름: {0}")
	@NullSource
	@ValueSource(strings = {"욕욕욕"})
	void createInvalidName(String productName) {
		// given
		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setQuantity(1);

		List<MenuProduct> menuProducts = new ArrayList<>();
		menuProducts.add(menuProduct);

		Product product = new Product();
		product.setPrice(BigDecimal.valueOf(3000));

		List<Product> products = new ArrayList<>();
		products.add(product);

		Menu request = new Menu();
		request.setPrice(BigDecimal.valueOf(2500));
		request.setMenuGroupId(UUID.randomUUID());
		request.setMenuProducts(menuProducts);
		request.setName(productName);

		MenuGroup menuGroup = new MenuGroup();

		given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
		given(productRepository.findAllByIdIn(any())).willReturn(products);
		given(productRepository.findById(any())).willReturn(Optional.of(product));
		given(purgomalumClient.containsProfanity(productName)).willReturn(true);

		// when then
		assertThatThrownBy(() -> menuService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴 가격을 변경할 수 있다")
	@ParameterizedTest(name = "메뉴 가격: {0}")
	@ValueSource(strings = {"0", "11", "200"})
	void changePrice(BigDecimal price) {
		// given
		Menu request = aMenu().withPrice(price).build();

		Menu menu = aDefaultMenu().build();
		UUID menuId = menu.getId();

		given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

		// when
		Menu result = menuService.changePrice(menuId, request);

		// then
		assertThat(result.getPrice()).isEqualTo(price);
	}

	@DisplayName("메뉴 가격 변경 시 메뉴 가격이 0보다 작은 경우 예외가 발생한다")
	@ParameterizedTest(name = "메뉴 가격: {0}")
	@NullSource
	@ValueSource(strings = {"-1"})
	void changePriceInvalidPrice(BigDecimal price) {
		// given
		UUID menuId = UUID.randomUUID();
		Menu request = aMenu().withPrice(price).build();

		// when then
		assertThatThrownBy(() -> menuService.changePrice(menuId, request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("존재하지 않는 메뉴의 가격을 변경하는 경우 예외가 발생한다")
	@Test
	void changePriceNotExistMenu() {
		// given
		Menu request = aMenu().withPrice(DEFAULT_MENU_PRICE).build();
		UUID notExistedMenuId = UUID.randomUUID();

		given(menuRepository.findById(notExistedMenuId)).willReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> menuService.changePrice(notExistedMenuId, request))
				.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("메뉴 상품들의 가격 합보다 큰 금액으로 메뉴 가격을 변경 시 예외가 발생한다")
	@Test
	void changePriceInvalidSum() {
		// given
		BigDecimal menuPriceMoreExpensiveThanMenuProductsTotalPrice = aMenuThatMoreExpensivePriceThanMenuProductsTotalPrice().build().getPrice();
		Menu request = aMenu().withPrice(menuPriceMoreExpensiveThanMenuProductsTotalPrice).build();

		Menu menu = aMenuThatSamePriceWithMenuProductsTotalPrice().build();
		UUID menuId = menu.getId();

		given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

		// when then
		assertThatThrownBy(() -> menuService.changePrice(menuId, request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴를 전시한다")
	@ParameterizedTest(name = "메뉴 전시중: {0}")
	@ValueSource(booleans = {true, false})
	void display(boolean displayed) {
		// given
		Menu menu = aDefaultMenu().withDisplayed(displayed).build();
		UUID menuId = menu.getId();

		given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

		// when
		Menu result = menuService.display(menuId);

		// then
		assertThat(result.isDisplayed()).isTrue();
	}

	@DisplayName("존재하지 않는 메뉴를 전시하는 경우 예외가 발생한다")
	@Test
	void displayNotExistMenu() {
		// given
		UUID notExistedMenuId = UUID.randomUUID();

		given(menuRepository.findById(notExistedMenuId)).willReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> menuService.display(notExistedMenuId))
				.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("메뉴 가격이 메뉴상품들의 가격 합보다 큰 메뉴를 전시하는 경우 예외가 발생한다")
	@Test
	void displayInvalidSum() {
		// given
		Menu menu = aMenuThatMoreExpensivePriceThanMenuProductsTotalPrice().build();
		UUID menuId = menu.getId();

		given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

		// when then
		assertThatThrownBy(() -> menuService.display(menuId))
				.isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("메뉴를 전시하지 않는다")
	@ParameterizedTest(name = "메뉴 전시중: {0}")
	@ValueSource(booleans = {true, false})
	void hide(boolean displayed) {
		// given
		Menu menu = aDefaultMenu().withDisplayed(displayed).build();
		UUID menuId = menu.getId();

		given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

		// when
		Menu result = menuService.hide(menuId);

		// then
		assertThat(result.isDisplayed()).isFalse();
	}

	@DisplayName("존재하지 않는 메뉴를 전시하지 않도록 하는 경우 예외가 발생한다")
	@Test
	void hideNotExist() {
		// given
		UUID notExistedMenuId = UUID.randomUUID();

		given(menuRepository.findById(notExistedMenuId)).willReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> menuService.hide(notExistedMenuId))
				.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("모든 메뉴들을 조회한다")
	@Test
	void findAll() {
		// given
		Menu menu1 = aDefaultMenu().build();
		Menu menu2 = aDefaultMenu().build();

		List<Menu> menus = Arrays.asList(menu1, menu2);

		given(menuRepository.findAll()).willReturn(menus);

		// when
		List<Menu> result = menuService.findAll();

		// then
		assertThat(result).isSameAs(menus);
	}
}
