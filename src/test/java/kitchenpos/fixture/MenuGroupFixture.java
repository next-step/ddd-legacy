package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

import static kitchenpos.TestConstant.한마리메뉴_MENU_GROUP_NAME;
import static kitchenpos.TestConstant.후라이드치킨_MENU_GROUP_UUID;

public class MenuGroupFixture {
    public static MenuGroup createMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(한마리메뉴_MENU_GROUP_NAME);
        menuGroup.setId(후라이드치킨_MENU_GROUP_UUID);
        return menuGroup;
    }

    public static MenuGroup createMenuGroup(final String name, final UUID id) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        menuGroup.setId(id);
        return menuGroup;
    }

}
