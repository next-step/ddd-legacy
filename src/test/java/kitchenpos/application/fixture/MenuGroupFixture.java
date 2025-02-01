package kitchenpos.application.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    public static final MenuGroup DEFAULT_MENUGROUP = setMenuGroup("기본메뉴그룹");

    public static MenuGroup setMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        menuGroup.setId(UUID.randomUUID());
        return menuGroup;
    }
}
