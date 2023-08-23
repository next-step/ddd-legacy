package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    private static final String DEFAULT_NAME = "기본메뉴그룹";
    public static MenuGroup create(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup createDefault() {
        return create(DEFAULT_NAME);
    }
}
