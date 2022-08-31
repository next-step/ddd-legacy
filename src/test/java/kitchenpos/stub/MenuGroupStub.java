package kitchenpos.stub;

import java.util.UUID;

import kitchenpos.domain.MenuGroup;

public class MenuGroupStub {

	private static final String DEFAULT_MENU_GROUP_NAME = "기본-메뉴-그룹";

	private MenuGroupStub() {
	}

	public static MenuGroup createDefault() {
		return createCustom(DEFAULT_MENU_GROUP_NAME);
	}

	public static MenuGroup createCustom(String name) {
		MenuGroup menuGroup = new MenuGroup();
		menuGroup.setName(name);
		menuGroup.setId(UUID.randomUUID());
		return menuGroup;
	}
}
