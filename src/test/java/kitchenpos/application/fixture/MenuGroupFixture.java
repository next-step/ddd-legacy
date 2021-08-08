package kitchenpos.application.fixture;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    private static final String MENU_GROUP_NAME_1 = "메뉴그룹1";
    private static final UUID UUID1 = UUID.randomUUID();
    private static final UUID UUID2 = UUID.randomUUID();

    public static MenuGroup MENU_GROUP1() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID1);
        menuGroup.setName(MENU_GROUP_NAME_1);
        return menuGroup;
    }

    public static MenuGroup MENU_GROUP2_SAME_NAME() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID2);
        menuGroup.setName(MENU_GROUP_NAME_1);
        return menuGroup;
    }

    public static MenuGroup MENU_GROUP_WITH_NAME(final String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID1);
        menuGroup.setName(name);
        return menuGroup;
    }

}
