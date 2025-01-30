package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    private static final String DEFAULT_MENU_GROUP_NAME = "메뉴 그룹";

    private MenuGroupFixture() {
    }

    public static MenuGroup menuGroup() {
        return menuGroup(getUuid(), DEFAULT_MENU_GROUP_NAME);
    }

    public static MenuGroup menuGroup(final UUID uuid) {
        return menuGroup(uuid, DEFAULT_MENU_GROUP_NAME);
    }

    public static MenuGroup menuGroup(final String name) {
        return menuGroup(UUID.randomUUID(), name);
    }

    public static MenuGroup menuGroup(final UUID uuid, final String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(uuid);
        menuGroup.setName(name);
        return menuGroup;
    }

    private static UUID getUuid() {
        return UUID.randomUUID();
    }
}
