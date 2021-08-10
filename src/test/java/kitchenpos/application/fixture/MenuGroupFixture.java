package kitchenpos.application.fixture;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    private static final String MENU_GROUP_NAME_1 = "메뉴그룹1";
    private static final String MENU_GROUP_NAME_2 = "메뉴그룹2";
    private static final UUID UUID1 = UUID.randomUUID();
    private static final UUID UUID2 = UUID.randomUUID();

    public static MenuGroup MENU_GROUP1_REQUEST() {
        return createMenuGroup(null, MENU_GROUP_NAME_1);
    }

    public static MenuGroup MENU_GROUP2_REQUEST_SAME_NAME() {
        return createMenuGroup(null, MENU_GROUP_NAME_1);
    }

    public static MenuGroup MENU_GROUP1() {
        return createMenuGroup(UUID1, MENU_GROUP_NAME_1);
    }

    public static MenuGroup MENU_GROUP2() {
        return createMenuGroup(UUID2, MENU_GROUP_NAME_2);
    }

    public static MenuGroup MENU_GROUP2_SAME_NAME() {
        return createMenuGroup(UUID2, MENU_GROUP_NAME_1);
    }

    public static MenuGroup MENU_GROUP_WITH_NAME_REQUEST(final String name) {
        return createMenuGroup(null, name);
    }

    private static MenuGroup createMenuGroup(final UUID id, final String menuGroupName) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(menuGroupName);
        return menuGroup;
    }

}
