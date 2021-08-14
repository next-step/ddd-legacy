package kitchenpos.application.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    private static final String MENU_GROUP_NAME_ONE = "메뉴그룹1";
    private static final String MENU_GROUP_NAME_TWO = "메뉴그룹2";
    private static final UUID UUID1 = UUID.randomUUID();
    private static final UUID UUID2 = UUID.randomUUID();

    public static MenuGroup MENU_GROUP_ONE_REQUEST() {
        return createMenuGroup(UUID1, MENU_GROUP_NAME_ONE);
    }

    private static MenuGroup createMenuGroup(final UUID uuid, final String menuGroupName) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(uuid);
        menuGroup.setName(menuGroupName);

        return menuGroup;
    }
}
