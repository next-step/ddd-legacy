package kitchenpos.application;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.ProductFixture;

public class MenuServiceIntegrationTest extends IntegrationTest {
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
	void 메뉴_생성() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));

		MenuProduct givenMenuProductRequest = new MenuProduct();
		givenMenuProductRequest.setProductId(givenProduct.getId());
		givenMenuProductRequest.setQuantity(2);

		Menu givenRequest = new Menu();
		givenRequest.setName("후라이드+후라이드");
		givenRequest.setPrice(new BigDecimal(19000));
		givenRequest.setMenuGroupId(givenMenuGroup.getId());
		givenRequest.setDisplayed(true);
		givenRequest.setMenuProducts(Collections.singletonList(givenMenuProductRequest));

		// when
		Menu actualMenu = menuService.create(givenRequest);

		// then
		Assertions.assertThat(actualMenu.getId()).isNotNull();
	}

	@DisplayName("메뉴 생성 실패 : 가격 음수")
	@Test
	void 메뉴_생성_실패_1() {
		// given
		Menu givenRequest = new Menu();
		givenRequest.setPrice(new BigDecimal(-10000)); // negative

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 생성 실패 : 메뉴 그룹 없음")
	@Test
	void 메뉴_생성_실패_2() {
		// given
		Menu givenRequest = new Menu();
		givenRequest.setPrice(new BigDecimal(19000));
		givenRequest.setMenuGroupId(UUID.randomUUID()); // unknown

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(givenRequest);

		// then
		Assertions.assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 생성 실패 : 메뉴 상품 없음")
	@Test
	void 메뉴_생성_실패_3() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());

		Menu givenRequest = new Menu();
		givenRequest.setPrice(new BigDecimal(19000));
		givenRequest.setMenuGroupId(givenMenuGroup.getId());
		givenRequest.setMenuProducts(null); // empty

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 생성 실패 : 메뉴 상품의 상품 식별자 유효하지 않음")
	@Test
	void 메뉴_생성_실패_4() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());

		MenuProduct givenMenuProductRequest = new MenuProduct();
		givenMenuProductRequest.setProductId(UUID.randomUUID()); // unknown id

		Menu givenRequest = new Menu();
		givenRequest.setPrice(new BigDecimal(19000));
		givenRequest.setMenuGroupId(givenMenuGroup.getId());
		givenRequest.setMenuProducts(Collections.singletonList(givenMenuProductRequest));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 생성 실패 : 메뉴 상품 수량 음수")
	@Test
	void 메뉴_생성_실패_5() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));

		MenuProduct givenMenuProductRequest = new MenuProduct();
		givenMenuProductRequest.setProductId(givenProduct.getId());
		givenMenuProductRequest.setQuantity(-2); // negative

		Menu givenRequest = new Menu();
		givenRequest.setPrice(new BigDecimal(19000));
		givenRequest.setMenuGroupId(givenMenuGroup.getId());
		givenRequest.setMenuProducts(Collections.singletonList(givenMenuProductRequest));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 생성 실패 : 메뉴의 가격 > 메뉴 상품들의 (가격 * 수량)")
	@Test
	void 메뉴_생성_실패_6() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));

		MenuProduct givenMenuProductRequest = new MenuProduct();
		givenMenuProductRequest.setProductId(givenProduct.getId());
		givenMenuProductRequest.setQuantity(2);

		Menu givenRequest = new Menu();
		givenRequest.setPrice(new BigDecimal(100000000)); // high
		givenRequest.setMenuGroupId(givenMenuGroup.getId());
		givenRequest.setMenuProducts(Collections.singletonList(givenMenuProductRequest));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 생성 실패 : 이름 빈값")
	@Test
	void 메뉴_생성_실패_7() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));

		MenuProduct givenMenuProductRequest = new MenuProduct();
		givenMenuProductRequest.setProductId(givenProduct.getId());
		givenMenuProductRequest.setQuantity(2);

		Menu givenRequest = new Menu();
		givenRequest.setPrice(new BigDecimal(19000));
		givenRequest.setMenuGroupId(givenMenuGroup.getId());
		givenRequest.setMenuProducts(Collections.singletonList(givenMenuProductRequest));
		givenRequest.setName(null); // empty

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 생성 실패 : 이름 비속어")
	@Test
	void 메뉴_생성_실패_8() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));

		MenuProduct givenMenuProductRequest = new MenuProduct();
		givenMenuProductRequest.setProductId(givenProduct.getId());
		givenMenuProductRequest.setQuantity(2);

		Menu givenRequest = new Menu();
		givenRequest.setPrice(new BigDecimal(19000));
		givenRequest.setMenuGroupId(givenMenuGroup.getId());
		givenRequest.setMenuProducts(Collections.singletonList(givenMenuProductRequest));
		givenRequest.setName("fuck"); // profanity

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 가격 변경")
	@Test
	void 메뉴_가격_변경() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		Menu givenMenu = menuRepository.save(MenuFixture.menu(new BigDecimal(19000), givenMenuGroup, givenProduct));

		Menu givenRequest = new Menu();
		givenRequest.setPrice(new BigDecimal(15000));

		// when
		Menu actualMenu = menuService.changePrice(givenMenu.getId(), givenRequest);

		// then
		Assertions.assertThat(actualMenu.getPrice()).isEqualTo(new BigDecimal(15000));
	}

	@DisplayName("메뉴 가격 변경 실패 : 가격 음수")
	@Test
	void 메뉴_가격_변경_실패_1() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		Menu givenMenu = menuRepository.save(MenuFixture.menu(new BigDecimal(19000), givenMenuGroup, givenProduct));

		Menu givenRequest = new Menu();
		givenRequest.setPrice(new BigDecimal(-10000)); // negative

		// when
		ThrowableAssert.ThrowingCallable throwingCallable =
			() -> menuService.changePrice(givenMenu.getId(), givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 가격 변경 실패 : 메뉴의 가격 > 메뉴 상품들의 (가격 * 수량)")
	@Test
	void 메뉴_가격_변경_실패_2() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		Menu givenMenu = menuRepository.save(MenuFixture.menu(new BigDecimal(19000), givenMenuGroup, givenProduct));

		Menu givenRequest = new Menu();
		givenRequest.setPrice(new BigDecimal(100000000)); // high

		// when
		ThrowableAssert.ThrowingCallable throwingCallable =
			() -> menuService.changePrice(givenMenu.getId(), givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 전시")
	@Test
	void 메뉴_전시() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		Menu givenMenu = menuRepository.save(MenuFixture.menu(new BigDecimal(19000), givenMenuGroup, givenProduct));

		// when
		Menu actualMenu = menuService.display(givenMenu.getId());

		// then
		Assertions.assertThat(actualMenu.isDisplayed()).isEqualTo(true);
	}

	@DisplayName("메뉴 전시 실패 : 메뉴의 가격 > 메뉴 상품들의 (가격 * 수량)")
	@Test
	void 메뉴_전시_실패() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		Menu givenMenu = menuRepository.save(MenuFixture.menu(new BigDecimal(100000000), givenMenuGroup, givenProduct));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.display(givenMenu.getId());

		// then
		Assertions.assertThatExceptionOfType(IllegalStateException.class).isThrownBy(throwingCallable);
	}

	@DisplayName("메뉴 숨김")
	@Test
	void 메뉴_숨김() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		Menu givenMenu = menuRepository.save(MenuFixture.menu(new BigDecimal(19000), givenMenuGroup, givenProduct));

		// when
		Menu actualMenu = menuService.hide(givenMenu.getId());

		// then
		Assertions.assertThat(actualMenu.isDisplayed()).isEqualTo(false);
	}

	@DisplayName("전체 메뉴 조회")
	@Test
	void 전체_메뉴_조회() {
		// given
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		menuRepository.save(MenuFixture.menu(new BigDecimal(19000), givenMenuGroup, givenProduct));

		// when
		List<Menu> actual = menuService.findAll();

		// then
		Assertions.assertThat(actual).isNotEmpty();
	}
}
