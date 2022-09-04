package kitchenpos.application.fixture;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupMother {

	private static final Map<String, MenuGroup> values = new HashMap<>();

	static {
		values.put("추천메뉴그룹", 추천메뉴그룹());
		values.put("이름이 빈 메뉴그룹", 이름이_빈_메뉴그룹());
	}

	public static MenuGroup findByName(String name) {
		return values.get(name);
	}

	private static MenuGroup 추천메뉴그룹() {
		MenuGroup menuGroup = new MenuGroup();
		menuGroup.setName("추천메뉴그룹");

		return menuGroup;
	}

	private static MenuGroup 이름이_빈_메뉴그룹() {
		MenuGroup menuGroup = new MenuGroup();
		menuGroup.setId(UUID.randomUUID());
		menuGroup.setName("");

		return menuGroup;
	}
}
