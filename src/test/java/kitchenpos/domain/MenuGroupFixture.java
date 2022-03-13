package kitchenpos.domain;

import java.util.UUID;

public class MenuGroupFixture {

    public static final MenuGroup CHICKEN_MENU_GROUP = create("chicken");

    public static MenuGroup create(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }
}
