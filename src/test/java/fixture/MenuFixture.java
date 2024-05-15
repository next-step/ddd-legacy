package fixture;

import static fixture.ProductFixture.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuFixture {
	private static final String VALID_MENU_GROUP_NAME = "점심특선";
	private static final String VALID_MENU_NAME = "버거세트";
	public static final String MENU_NAME_WITH_PROFANITY = "비속어가 포함된 메뉴명";
	private static final int VALID_MENU_PRODUCT_QUANTITY = 2;
	private static final BigDecimal VALID_MENU_PRICE = new BigDecimal("17.98");

	public static MenuGroup createValidMenuGroup() {
		MenuGroup validMenuGroup = new MenuGroup();
		validMenuGroup.setId(UUID.randomUUID());
		validMenuGroup.setName(VALID_MENU_GROUP_NAME);
		return validMenuGroup;
	}

	public static MenuProduct createValidMenuProductWithQuantity(int quantity) {
		Product validProduct = ProductFixture.create(VALID_PRODUCT_NAME, VALID_PRODUCT_PRICE);

		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setProduct(validProduct);
		menuProduct.setProductId(validProduct.getId());
		menuProduct.setQuantity(quantity);
		return menuProduct;
	}

	public static Menu create(
		MenuGroup menuGroup,
		String name,
		BigDecimal price,
		List<MenuProduct> menuProducts,
		boolean displayed) {
		Menu menu = new Menu();
		menu.setMenuGroupId(menuGroup.getId());
		menu.setMenuGroup(menuGroup);
		menu.setId(UUID.randomUUID());
		menu.setName(name);
		menu.setPrice(price);
		menu.setMenuProducts(menuProducts);
		menu.setDisplayed(displayed);
		return menu;
	}

	public static Menu createValid() {
		return create(
			createValidMenuGroup(),
			VALID_MENU_NAME,
			VALID_MENU_PRICE,
			List.of(createValidMenuProductWithQuantity(VALID_MENU_PRODUCT_QUANTITY)),
			true
		);
	}

	public static Menu createWithNameAndPrice(String name, BigDecimal price) {
		Menu validMenu = createValid();
		validMenu.setName(name);
		validMenu.setPrice(price);
		return validMenu;
	}

	public static Menu createWithPrice(BigDecimal price) {
		Menu validMenu = createValid();
		validMenu.setPrice(price);
		return validMenu;
	}
}
