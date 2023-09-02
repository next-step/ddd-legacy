package kitchenpos.testfixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    private MenuGroupFixture() {

    }

    public static MenuGroup createMenuGroup(String name) {
        return createMenuGroup(UUID.randomUUID(), name);
    }

    public static MenuGroup createMenuGroup(UUID id, String name) {
        var menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

}
