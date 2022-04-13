package kitchenpos.domain;

import java.util.UUID;

public class MenuGroupTest {

    public static MenuGroup createMenu(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }
}
