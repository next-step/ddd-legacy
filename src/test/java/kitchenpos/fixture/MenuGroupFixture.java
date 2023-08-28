package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    private static String MENU_GROUP_NAME = "메뉴그룹1";
    public static MenuGroup MENU_GROUP() {
        MenuGroup menuGroup = new MenuGroup();

        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(MENU_GROUP_NAME);
        return menuGroup;
    }
}
