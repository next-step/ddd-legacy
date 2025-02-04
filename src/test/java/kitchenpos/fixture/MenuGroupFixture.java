package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    public static final String DEFAULT_MENU_GROUP_NAME = "치킨 메뉴";

    private MenuGroupFixture() {
    }

    public static MenuGroup menuGroup(final UUID id, final String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup menuGroup() {
        return menuGroup(getUuid(), DEFAULT_MENU_GROUP_NAME);
    }

    private static UUID getUuid() {
        return UUID.randomUUID();
    }

}
