package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {
	public static MenuGroup MENU_GROUP() {
		MenuGroup menuGroup = new MenuGroup();
		menuGroup.setId(UUID.randomUUID());
		menuGroup.setName("추천메뉴");
		return menuGroup;
	}
}
