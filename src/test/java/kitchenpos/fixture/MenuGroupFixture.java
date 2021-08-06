package kitchenpos.fixture;

import java.util.UUID;

import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {
	public static MenuGroup menuGroup() {
		MenuGroup menuGroup = new MenuGroup();
		menuGroup.setId(UUID.randomUUID());
		menuGroup.setName("추천메뉴");
		return menuGroup;
	}
}
