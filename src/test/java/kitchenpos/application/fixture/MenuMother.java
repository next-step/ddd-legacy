package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

public class MenuMother {

	public static final Map<String, CreateMenu> creators = new HashMap<>();

	static {
		creators.put("추천메뉴", (name, menuGroup, menuProducts) -> 메뉴(name, 9000L, menuGroup, menuProducts));
		creators.put("가격이 빈 메뉴", (name, menuGroup, menuProducts) -> 메뉴(name, null, menuGroup, menuProducts));
		creators.put("가격이 음수인 메뉴", (name, menuGroup, menuProducts) -> 메뉴(name, -10000L, menuGroup, menuProducts));
		creators.put("비싼메뉴", (name, menuGroup, menuProducts) -> 메뉴(name, 1000000L, menuGroup, menuProducts));
		creators.put("이름이 빈 메뉴", (name, menuGroup, menuProducts) -> 메뉴("", 10000L, menuGroup, menuProducts));
		creators.put("욕설이 포함된 메뉴", (name, menuGroup, menuProducts) -> 메뉴("suck", 10000L, menuGroup, menuProducts));
		creators.put("메뉴그룹이 없는 메뉴", (name, menuGroup, menuProducts) -> 메뉴(name, 10000L, menuGroup, menuProducts));
		creators.put("메뉴 상품이 없는 메뉴", (name, menuGroup, menuProducts) -> 메뉴(name, 10000L, menuGroup, menuProducts));
	}

	public static CreateMenu findCreatorByName(String name) {
		return creators.get(name);
	}

	private static Menu 메뉴(
		String name,
		Long price,
		MenuGroup menuGroup,
		List<MenuProduct> menuProducts
	) {
		Menu menu = new Menu();
		menu.setId(UUID.randomUUID());
		menu.setName(name);
		if (price != null) {
			menu.setPrice(BigDecimal.valueOf(price));
		}
		menu.setMenuGroup(menuGroup);
		menu.setDisplayed(true);
		menu.setMenuProducts(menuProducts);
		if (menuGroup != null) {
			menu.setMenuGroupId(menuGroup.getId());
		}

		return menu;
	}

	public interface CreateMenu {

		Menu create(String name, MenuGroup menuGroup, List<MenuProduct> menuProducts);
	}
}
