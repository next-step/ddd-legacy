package kitchenpos.util;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFactory {
    private MenuGroupFactory() {
    }

    public static MenuGroup createMenuGroup(UUID id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }
}
