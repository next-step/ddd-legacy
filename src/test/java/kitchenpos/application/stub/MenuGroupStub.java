package kitchenpos.application.stub;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupStub {

    private static final String DEFAULT_NAME = "추천메뉴";

    public static MenuGroup createDefault() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(DEFAULT_NAME);
        return menuGroup;
    }

    public static MenuGroup create(final String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }
}
