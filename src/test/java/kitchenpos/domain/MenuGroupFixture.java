package kitchenpos.domain;

import java.util.UUID;

public class MenuGroupFixture {

    public static MenuGroup MenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup MenuGroupWithUUID(String name) {
        MenuGroup menuGroup = MenuGroup(name);
        menuGroup.setId(UUID.randomUUID());
        return menuGroup;
    }
}
