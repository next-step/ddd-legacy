package kitchenpos.dummy;

import kitchenpos.domain.MenuGroup;

public class DummyMenuGroup {

    public static MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup createMenuGroup() {
        return createMenuGroup("기본 메뉴 그룹");
    }
}
