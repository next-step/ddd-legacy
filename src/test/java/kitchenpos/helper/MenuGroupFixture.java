package kitchenpos.helper;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    private static final String DEFAULT_MENU_GROUP_NAME = "default menu group name";

    public static MenuGroup create(String name) {
        var menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup create() {
        return create(DEFAULT_MENU_GROUP_NAME);
    }
}
