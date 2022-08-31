package kitchenpos.stub;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

public class MenuStub {

	private static final String DEFAULT_NAME = "기본 메뉴";
	private static final boolean DEFAULT_DISPLAYED = true;
	private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(1000);

	private MenuStub() {
	}

	public static Menu createDefault() {
		return createCustom(
			DEFAULT_NAME,
			DEFAULT_PRICE,
			DEFAULT_DISPLAYED,
			MenuGroupStub.createDefault(),
			MenuProductStub.createDefaultList()
		);
	}

	public static Menu createNonDisplayed() {
		return createCustom(
			DEFAULT_NAME,
			DEFAULT_PRICE,
			!DEFAULT_DISPLAYED,
			MenuGroupStub.createDefault(),
			MenuProductStub.createDefaultList()
		);
	}

	public static Menu createCustom(String name, BigDecimal price, boolean displayed, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
		Menu menu = new Menu();
		menu.setId(UUID.randomUUID());
		menu.setName(name);
		menu.setPrice(price);
		menu.setDisplayed(displayed);
		menu.setMenuGroup(menuGroup);
		menu.setMenuProducts(menuProducts);
		return menu;
	}
}
