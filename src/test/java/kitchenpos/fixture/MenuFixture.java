package kitchenpos.fixture;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuFixture {
	public static Menu menu(BigDecimal price, MenuGroup menuGroup, Product product) {
		Menu menu = new Menu();
		menu.setId(UUID.randomUUID());
		menu.setName("후라이드+후라이드");
		menu.setPrice(price);
		menu.setMenuGroup(menuGroup);
		menu.setDisplayed(true);
		menu.setMenuProducts(Collections.singletonList(menuProduct(product)));
		return menu;
	}

	public static Menu hidden(BigDecimal price, MenuGroup menuGroup, Product product) {
		Menu menu = new Menu();
		menu.setId(UUID.randomUUID());
		menu.setName("후라이드+후라이드");
		menu.setPrice(price);
		menu.setMenuGroup(menuGroup);
		menu.setDisplayed(false);
		menu.setMenuProducts(Collections.singletonList(menuProduct(product)));
		return menu;
	}

	private static MenuProduct menuProduct(Product product) {
		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setProduct(product);
		menuProduct.setQuantity(2);
		return menuProduct;
	}
}
