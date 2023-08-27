package kitchenpos.helper;


import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public final class MenuGroupHelper {

    private MenuGroupHelper() {
    }

    public static MenuGroup create(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

}
