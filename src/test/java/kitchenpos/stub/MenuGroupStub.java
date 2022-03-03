package kitchenpos.stub;

import kitchenpos.domain.MenuGroup;

public class MenuGroupStub {

    public static final String MENU_GROUP_TEST_NAME = "테스트메뉴그룹";

    private MenuGroupStub() {
    }

    public static MenuGroup generateTestMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(MENU_GROUP_TEST_NAME);
        return menuGroup;
    }
}
