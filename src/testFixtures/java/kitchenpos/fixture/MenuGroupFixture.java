package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    private MenuGroupFixture() {}

    private static final String DEFAULT_NAME = "menu_group";

    public static MenuGroup generateMenuGroup() {
        return createMenuGroup(UUID.randomUUID(), DEFAULT_NAME);
    }

    public static MenuGroup generateMenuGroupWithName(final String name) {
        return createMenuGroup(UUID.randomUUID(), name);
    }

    private static MenuGroup createMenuGroup(final UUID id, final String name) {
        MenuGroup menuGroup = new MenuGroup();

        menuGroup.setId(id);
        menuGroup.setName(name);

        return menuGroup;
    }
}
