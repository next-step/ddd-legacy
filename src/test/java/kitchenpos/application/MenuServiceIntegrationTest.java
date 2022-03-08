package kitchenpos.application;

import kitchenpos.IntegrationTest;
import kitchenpos.argument.NullAndNegativeBigDecimalArgumentsProvider;
import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.ProductFixture;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MenuServiceIntegrationTest extends IntegrationTest {
	private static final String MENU_NAME = "후라이드+후라이드";
	private static final BigDecimal MENU_PRICE = new BigDecimal(19000);
	private static final BigDecimal MENU_LOW_PRICE = new BigDecimal(15000);
	private static final BigDecimal MENU_HIGH_PRICE = new BigDecimal(100000000);
	private static final BigDecimal PRODUCT_PRICE = new BigDecimal(17000);
	private static final long PRODUCT_QUANTITY = 2;
	private static final long PRODUCT_NEGATIVE_QUANTITY = -2;
	private static final String PROFANITY = "fuck";

	@Autowired
	private MenuService menuService;
	@Autowired
	private MenuGroupRepository menuGroupRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private MenuRepository menuRepository;

	@DisplayName("메뉴 생성")
	@Test
	void createMenu() {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Product product = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));

		MenuProduct givenMenuProductRequest = new MenuProduct();
		givenMenuProductRequest.setProductId(product.getId());
		givenMenuProductRequest.setQuantity(PRODUCT_QUANTITY);

		Menu menu = new Menu();
		menu.setName(MENU_NAME);
		menu.setPrice(MENU_PRICE);
		menu.setMenuGroupId(menuGroup.getId());
		menu.setDisplayed(true);
		menu.setMenuProducts(Collections.singletonList(givenMenuProductRequest));

		// when
		Menu actualMenu = menuService.create(menu);

		// then
		assertAll(
			() -> assertThat(actualMenu.getId()).isNotNull(),
			() -> assertThat(actualMenu.getName()).isEqualTo(menu.getName()),
			() -> assertThat(actualMenu.getPrice()).isEqualTo(menu.getPrice()),
			() -> assertThat(actualMenu.getMenuGroup().getId()).isEqualTo(menu.getMenuGroupId()),
			() -> assertThat(actualMenu.isDisplayed()).isEqualTo(menu.isDisplayed()),
			() -> {
				List<MenuProduct> actualMenuProducts = actualMenu.getMenuProducts();
				MenuProduct actualMenuProduct = actualMenuProducts.get(0);
				assertAll(
					() -> assertThat(actualMenuProduct.getProduct().getId()).isEqualTo(
						givenMenuProductRequest.getProductId()),
					() -> assertThat(actualMenuProduct.getQuantity()).isEqualTo(givenMenuProductRequest.getQuantity())
				);
			}
		);
	}

	@DisplayName("가격이 널 또는 음수이면 메뉴 생성 실패")
	@ParameterizedTest
	@ArgumentsSource(NullAndNegativeBigDecimalArgumentsProvider.class)
	void failCreatingMenuWhenPriceIsNullOrNegative(BigDecimal price) {
		// given
		Menu menu = new Menu();
		menu.setPrice(price);

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(menu);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 그룹 없으면 메뉴 생성 실패에 실패")
	@Test
	void failCreatingMenuWhenNonMenuGroup() {
		// given
		Menu menu = new Menu();
		menu.setPrice(MENU_PRICE);
		menu.setMenuGroupId(UUID.randomUUID());

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(menu);

		// then
		Assertions.assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 상품이 없으면 메뉴 생성 실패")
	@ParameterizedTest
	@NullAndEmptySource
	void failCreatingMenuWhenMenuProduct(List<MenuProduct> menuProductsRequest) {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());

		Menu menu = new Menu();
		menu.setPrice(MENU_PRICE);
		menu.setMenuGroupId(menuGroup.getId());
		menu.setMenuProducts(menuProductsRequest);

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(menu);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 상품의 상품 식별자 유효하지 않으면 메뉴 생성 실패")
	@Test
	void failCreatingMenuWhenMenuIdInvalid() {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());

		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setProductId(UUID.randomUUID()); // unknown id

		Menu menu = new Menu();
		menu.setPrice(MENU_PRICE);
		menu.setMenuGroupId(menuGroup.getId());
		menu.setMenuProducts(Collections.singletonList(menuProduct));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(menu);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 상품 수량 음수이면 메뉴 생성 실패")
	@Test
	void failCreatingMenuWhenMenuCountIsNegative() {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Product product = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));

		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setProductId(product.getId());
		menuProduct.setQuantity(PRODUCT_NEGATIVE_QUANTITY);

		Menu menu = new Menu();
		menu.setPrice(MENU_PRICE);
		menu.setMenuGroupId(menuGroup.getId());
		menu.setMenuProducts(Collections.singletonList(menuProduct));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(menu);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴의 가격 > 메뉴 상품들의 (가격 * 수량)이면 메뉴 생성 실패")
	@Test
	void failCreatingMenuWhenPrice() {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Product product = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));

		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setProductId(product.getId());
		menuProduct.setQuantity(PRODUCT_QUANTITY);

		Menu menu = new Menu();
		menu.setPrice(MENU_HIGH_PRICE);
		menu.setMenuGroupId(menuGroup.getId());
		menu.setMenuProducts(Collections.singletonList(menuProduct));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(menu);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("이름 null이면 메뉴 생성 실패")
	@Test
	void failCreatingMenuWhenMenuNameIsNull() {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Product product = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));

		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setProductId(product.getId());
		menuProduct.setQuantity(PRODUCT_QUANTITY);

		Menu menu = new Menu();
		menu.setPrice(MENU_PRICE);
		menu.setMenuGroupId(menuGroup.getId());
		menu.setMenuProducts(Collections.singletonList(menuProduct));
		menu.setName(null);

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(menu);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("이름 비속어이면 메뉴 생성 실패")
	@Test
	void failCreatingMenuWhenMenuNameIsProfanity() {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Product product = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));

		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setProductId(product.getId());
		menuProduct.setQuantity(PRODUCT_QUANTITY);

		Menu menu = new Menu();
		menu.setPrice(MENU_PRICE);
		menu.setMenuGroupId(menuGroup.getId());
		menu.setMenuProducts(Collections.singletonList(menuProduct));
		menu.setName(PROFANITY); // profanity

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(menu);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 가격 변경")
	@Test
	void changeMenuPrice() {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Product product = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, menuGroup, product));

		Menu newMenu = new Menu();
		newMenu.setPrice(MENU_LOW_PRICE);

		// when
		Menu actualMenu = menuService.changePrice(menu.getId(), newMenu);

		// then
		assertThat(actualMenu.getPrice()).isEqualTo(MENU_LOW_PRICE);
	}

	@DisplayName("가격이 널 또는 음수이면 메뉴 가격 변경 실패")
	@ParameterizedTest
	@ArgumentsSource(NullAndNegativeBigDecimalArgumentsProvider.class)
	void failChangingPriceWhenPriceIsNullOrNegative(BigDecimal price) {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Product product = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, menuGroup, product));

		Menu newMenu = new Menu();
		newMenu.setPrice(price);

		// when
		ThrowableAssert.ThrowingCallable throwingCallable =
			() -> menuService.changePrice(menu.getId(), newMenu);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴의 가격 > 메뉴 상품들의 (가격 * 수량)이면 메뉴 가격 변경 실패")
	@Test
	void failChangingPriceWhenPrice() {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Product product = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, menuGroup, product));

		Menu newMenu = new Menu();
		newMenu.setPrice(MENU_HIGH_PRICE); // high

		// when
		ThrowableAssert.ThrowingCallable throwingCallable =
			() -> menuService.changePrice(menu.getId(), newMenu);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 전시")
	@Test
	void visibleMenu() {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Product product = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, menuGroup, product));

		// when
		Menu actualMenu = menuService.display(menu.getId());

		// then
		assertThat(actualMenu.isDisplayed()).isTrue();
	}

	@DisplayName("메뉴의 가격 > 메뉴 상품들의 (가격 * 수량)이면 메뉴 전시 실패")
	@Test
	void failViewingMenu() {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Product product = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_HIGH_PRICE, menuGroup, product));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.display(menu.getId());

		// then
		Assertions.assertThatExceptionOfType(IllegalStateException.class).isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 숨김")
	@Test
	void hideMenu() {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Product product = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, menuGroup, product));

		// when
		Menu actualMenu = menuService.hide(menu.getId());

		// then
		assertThat(actualMenu.isDisplayed()).isFalse();
	}

	@DisplayName("전체 메뉴 조회")
	@Test
	void readAllMenu() {
		// given
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Product product = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, menuGroup, product));

		// when
		List<Menu> actual = menuService.findAll();

		// then
		List<UUID> actualIds = actual.stream().map(Menu::getId).collect(Collectors.toList());

		assertAll(
			() -> assertThat(actual).isNotEmpty(),
			() -> assertThat(actualIds).contains(menu.getId())
		);
	}
}
