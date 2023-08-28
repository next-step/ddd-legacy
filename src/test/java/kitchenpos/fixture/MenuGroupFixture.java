package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {
    public static MenuGroup createMenuGroup() {
        return createMenuGroup("치킨");
    }

    public static MenuGroup createMenuGroup(final String name) {
        return createMenuGroup(null, name);
    }

    public static MenuGroup createMenuGroup(final UUID id, final String name) {
        final MenuGroup menuGroup = new MenuGroup();

        menuGroup.setName(name);
        menuGroup.setId(id);

        return menuGroup;
    }
}
