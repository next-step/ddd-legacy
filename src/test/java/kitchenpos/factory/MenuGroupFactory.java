package kitchenpos.factory;

import kitchenpos.domain.MenuGroup;

public class MenuGroupFactory {

    public static final String DEFAULT_NAME = "치킨세트";

    public static MenuGroup getDefaultMenuGroup() {
        return MenuGroupFactory.of(DEFAULT_NAME);
    }

    public static MenuGroup of(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}
