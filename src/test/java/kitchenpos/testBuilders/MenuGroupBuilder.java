package kitchenpos.testBuilders;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupBuilder {
	public static final String DEFAULT_MENU_GROUP_NAME = "짬뽕";
	private UUID id;
	private String name;

	private MenuGroupBuilder() {
	}

	public static MenuGroupBuilder aMenuGroup() {
		return new MenuGroupBuilder();
	}

	public static MenuGroupBuilder aDefaultMenuGroup() {
		return aMenuGroup()
				.withId(UUID.randomUUID())
				.withName(DEFAULT_MENU_GROUP_NAME);
	}

	public MenuGroupBuilder withId(UUID id) {
		this.id = id;
		return this;
	}

	public MenuGroupBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public MenuGroup build() {
		MenuGroup menuGroup = new MenuGroup();
		menuGroup.setId(id);
		menuGroup.setName(name);
		return menuGroup;
	}
}
