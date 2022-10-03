package kitchenpos.testglue.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.application.MenuService;
import kitchenpos.application.fixture.MenuMother;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.util.testglue.TestGlueConfiguration;
import kitchenpos.util.testglue.TestGlueOperation;
import kitchenpos.util.testglue.TestGlueSupport;
import kitchenpos.util.testglue.test.TestGlueResponse;

@TestGlueConfiguration
public class MenuTestGlueConfiguration extends TestGlueSupport {

	private final MenuService menuService;
	private final MenuRepository menuRepository;

	public MenuTestGlueConfiguration(
		MenuService menuService,
		MenuRepository menuRepository
	) {
		this.menuService = menuService;
		this.menuRepository = menuRepository;
	}

	@TestGlueOperation("{}에 속하고 {} {}개를 이용해 {} 메뉴 데이터를 만들고")
	public void create_data(
		String menuGroupName,
		String productName,
		String productQuantity,
		String name
	) {
		MenuGroup menuGroup = getAsType(menuGroupName, MenuGroup.class);
		Product product = getAsType(productName, Product.class);

		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setProduct(product);
		menuProduct.setQuantity(toLong(productQuantity));
		if (product != null) {
			menuProduct.setProductId(product.getId());
		}

		Menu menuRequest = MenuMother.findCreatorByName(name).create(name, menuGroup, List.of(menuProduct));

		put(name, menuRequest);
	}

	@TestGlueOperation("{} 메뉴를 생성하고")
	public void create_request_given(String name) {
		Menu menu = getAsType(name, Menu.class);

		Menu savedMenu = menuService.create(menu);

		put(name, savedMenu);
	}

	@TestGlueOperation("{} 메뉴 생성을 요청하면")
	public void create_request_when(String name) {
		Menu menu = getAsType(name, Menu.class);

		TestGlueResponse<Menu> response = createResponse(() -> menuService.create(menu));

		put(name, response);
	}

	@TestGlueOperation("{} 메뉴가 생성된다")
	public void create(String name) {
		TestGlueResponse<Menu> response = getAsType(name, TestGlueResponse.class);

		Menu menu = response.getData();
		assertThat(menuRepository.findById(menu.getId())).isNotEmpty();
	}

	@TestGlueOperation("{} 메뉴 생성에 실패한다")
	public void create_fail(String name) {
		TestGlueResponse<Menu> response = getAsType(name, TestGlueResponse.class);

		assertThat(response.isOk()).isFalse();
	}

	@TestGlueOperation("{} 메뉴 가격을 {} 로 변경하고")
	public void changePrice_exist_menu_given(String name, String price) {
		changePrice_exist_menu_when(name, price);
		TestGlueResponse<Menu> changePriceResponse = getAsType("changePriceResponse", TestGlueResponse.class);
		Menu menu = changePriceResponse.getData();

		put(name, menu);
	}

	@TestGlueOperation("{} 메뉴 가격을 {} 로 변경하면")
	public void changePrice_exist_menu_when(String name, String price) {
		put("changePrice", toLong(price));

		Menu menu = getAsType(name, Menu.class);
		menu.setPrice(toBigDecimal(price));

		TestGlueResponse<Menu> response = createResponse(() -> menuService.changePrice(menu.getId(), menu));
		put("changePriceResponse", response);
	}

	@TestGlueOperation("없는메뉴 메뉴 가격을 {} 로 변경하면")
	public void changePrice_not_exist_menu(String price) {
		Menu menu = new Menu();
		menu.setPrice(toBigDecimal(price));
		TestGlueResponse<Menu> response = createResponse(() -> menuService.changePrice(UUID.randomUUID(), menu));
		put("changePriceResponse", response);
	}

	@TestGlueOperation("{} 메뉴를 전시상태로 변경하면")
	public void changeDisplay_true(String name) {
		Menu menu = getAsType(name, Menu.class);
		TestGlueResponse<Menu> response = createResponse(() -> menuService.display(menu.getId()));
		put("response", response);
	}

	@TestGlueOperation("{} 메뉴를 미전시상태로 변경하면")
	public void changeDisplay_false1(String name) {
		Menu menu = getAsType(name, Menu.class);
		TestGlueResponse<Menu> response = createResponse(() -> menuService.hide(menu.getId()));
		put("response", response);
	}

	@TestGlueOperation("{} 메뉴를 미전시상태로 변경하고")
	public void changeDisplay_false2(String name) {
		Menu menu = getAsType(name, Menu.class);
		menuService.hide(menu.getId());
		put(name, menu);
	}

	@TestGlueOperation("{} 메뉴 가격 변경에 실패한다")
	public void changePrice(String name) {
		TestGlueResponse<Menu> changePriceResponse = getAsType("changePriceResponse", TestGlueResponse.class);
		assertThat(changePriceResponse.isOk()).isFalse();

		Long price = getAsType("changePrice", Long.class);
		Menu menu = getAsType(name, Menu.class);
		Menu savedMenu = menuRepository.findById(menu.getId()).orElseThrow();

		assertThat(savedMenu.getPrice().longValue()).isNotEqualTo(price);
	}

	@TestGlueOperation("없는메뉴 메뉴 가격 변경에 실패한다")
	public void notExistMenu_changePrice() {
		TestGlueResponse<Menu> changePriceResponse = getAsType("changePriceResponse", TestGlueResponse.class);
		assertThat(changePriceResponse.isOk()).isFalse();
	}

	@TestGlueOperation("{} 메뉴가 전시상태로 변경된다")
	public void changeDisplay_true_response(String name) {
		TestGlueResponse<Menu> changePriceResponse = getAsType("response", TestGlueResponse.class);
		Menu menu = menuRepository.findById(getAsType(name, Menu.class).getId()).orElseThrow();

		assertAll(
			() -> assertThat(changePriceResponse.isOk()).isTrue(),
			() -> assertThat(menu.isDisplayed()).isTrue()
		);

	}

	@TestGlueOperation("{} 메뉴가 미전시상태로 변경된다")
	public void changeDisplay_false_response(String name) {
		TestGlueResponse<Menu> changePriceResponse = getAsType("response", TestGlueResponse.class);
		Menu menu = menuRepository.findById(getAsType(name, Menu.class).getId()).orElseThrow();

		assertAll(
			() -> assertThat(changePriceResponse.isOk()).isTrue(),
			() -> assertThat(menu.isDisplayed()).isFalse()
		);
	}

	@TestGlueOperation("{} 메뉴를 삭제하고")
	public void deleteMenu(String name) {
		// 제거가 어려움으로 UUID를 변경하여 비슷한 효과를 낸다.
		Menu menu = getAsType(name, Menu.class);
		menu.setId(UUID.randomUUID());
	}

	private Long toLong(String price) {
		try {
			return Long.parseLong(price);
		} catch (Exception e) {
			return null;
		}
	}

	private BigDecimal toBigDecimal(String price) {
		try {
			return BigDecimal.valueOf(Long.parseLong(price));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
