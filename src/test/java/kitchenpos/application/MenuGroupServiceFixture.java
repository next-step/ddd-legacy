package kitchenpos.application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kitchenpos.domain.MenuGroup;

public final class MenuGroupServiceFixture {

    private MenuGroupServiceFixture() {

    }

    public static MenuGroup menuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("인기 메뉴");
        return menuGroup;
    }

    public static List<MenuGroup> menuGroups() {
        List<MenuGroup> menuGroups = new ArrayList<>();
        menuGroups.add(menuGroup());
        return menuGroups;
    }

}
