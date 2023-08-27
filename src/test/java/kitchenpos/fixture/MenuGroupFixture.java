package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.List;
import java.util.UUID;

public class MenuGroupFixture {

    private static final UUID MENU_GROUP_ID = UUID.randomUUID();
    private static final String MENU_GROUP_NAME = "name";

    public static MenuGroup createMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName(MENU_GROUP_NAME);

        return menuGroup;
    }

    public static MenuGroup createMenuGroupWithName(final String name) {
        MenuGroup menuGroup = createMenuGroup();
        menuGroup.setName(name);

        return menuGroup;
    }

    public static List<MenuGroup> createMenuGroups() {
        return List.of(createMenuGroup(), createMenuGroup());
    }

}
