package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

import static kitchenpos.TestConstant.한마리메뉴_MENU_GROUP_NAME;
import static kitchenpos.TestConstant.후라이드치킨_MENU_GROUP_UUID;

public class MenuGroupFixture {
    private MenuGroupFixture() {
    }

    public static MenuGroup createMenuGroup() {
        return createMenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);
    }

    public static MenuGroup createMenuGroup(final String name, final UUID id) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        menuGroup.setId(id);
        return menuGroup;
    }

}
