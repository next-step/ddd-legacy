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
	private static final int VALID_MENU_PRODUCT_QUANTITY = 2;
	private static final BigDecimal VALID_MENU_PRICE = new BigDecimal("17.98");

	public static MenuGroup createValidMenuGroup() {
		MenuGroup validMenuGroup = new MenuGroup();
		validMenuGroup.setId(UUID.randomUUID());
		validMenuGroup.setName(VALID_MENU_GROUP_NAME);
		return validMenuGroup;
	}

	public static MenuProduct createValidMenuProduct(int quantity) {
		Product validProduct = ProductFixture.create(VALID_PRODUCT_NAME, VALID_PRODUCT_PRICE);

		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setProduct(validProduct);
		menuProduct.setProductId(validProduct.getId());
		menuProduct.setQuantity(quantity);
		return menuProduct;
	}

	public static Menu createValidMenu() {
		MenuGroup menuGroup = createValidMenuGroup();

		Menu menu = new Menu();
		menu.setMenuGroupId(menuGroup.getId());
		menu.setMenuGroup(menuGroup);
		menu.setId(UUID.randomUUID());
		menu.setName(VALID_MENU_NAME);
		menu.setPrice(VALID_MENU_PRICE);
		menu.setMenuProducts(
			List.of(createValidMenuProduct(VALID_MENU_PRODUCT_QUANTITY)));
		menu.setDisplayed(true);
		return menu;
	}

	public static Menu create(BigDecimal price) {
		Menu validMenu = createValidMenu();
		validMenu.setPrice(price);
		return validMenu;
	}
}
