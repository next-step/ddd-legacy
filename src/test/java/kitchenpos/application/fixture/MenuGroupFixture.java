package kitchenpos.application.fixture;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static final String MENU_GROUP_NAME_1 = "메뉴그룹1";

    public static final UUID FIXED_UUID = UUID.randomUUID();
    public static final UUID FIXED_UUID2 = UUID.randomUUID();

    public static MenuGroup MENU_GROUP1() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(FIXED_UUID);
        menuGroup.setName(MENU_GROUP_NAME_1);
        return menuGroup;
    }

    public static MenuGroup MENU_GROUP2() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(FIXED_UUID2);
        menuGroup.setName(MENU_GROUP_NAME_1);
        return menuGroup;
    }

    public static MenuGroup MENU_GROUP_WITH_NAME(final String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(FIXED_UUID);
        menuGroup.setName(name);
        return menuGroup;
    }

}
