package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {
    public static MenuGroup createMenuGroup() {
        return createMenuGroup("치킨");
    }

    public static MenuGroup createMenuGroup(final String name) {
        final MenuGroup menuGroup = new MenuGroup();

        menuGroup.setName(name);

        return menuGroup;
    }
}
