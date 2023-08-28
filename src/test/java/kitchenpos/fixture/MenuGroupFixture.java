package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {
    public static MenuGroup createMenuGroup() {
        final MenuGroup menuGroup = new MenuGroup();

        menuGroup.setName("치킨");
        menuGroup.setId(UUID.fromString("id"));

        return menuGroup;
    }
}
