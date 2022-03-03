package kitchenpos.stub;

import kitchenpos.domain.MenuGroup;

public class MenuGroupStub {

    public static final String FIRST_MENUGROUP_NAME = "첫번째테스트메뉴그룹";
    public static final String SECOND_MENUGROUP_NAME = "두번째테스트메뉴그룹";

    private MenuGroupStub() {
    }

    public static MenuGroup generateFirstTestMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(FIRST_MENUGROUP_NAME);
        return menuGroup;
    }

    public static MenuGroup generateSecondTestMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(SECOND_MENUGROUP_NAME);
        return menuGroup;
    }

    public static MenuGroup generateEmptyNameMenuGroup() {
        return new MenuGroup();
    }
}
