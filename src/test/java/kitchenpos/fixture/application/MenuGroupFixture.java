package kitchenpos.fixture.application;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    public static MenuGroup createMenuGroup(String menuGroupName) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(menuGroupName);
        menuGroup.setId(UUID.randomUUID());
        return menuGroup;
    }
}
