package kitchenpos.dummy;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class DummyMenuGroup {

    public static MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup defaultMenuGroup() {
        return createMenuGroup("메뉴그룹");
    }
}
