package kitchenpos.helper;


import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public final class MenuGroupHelper {

    public static final String DEFAULT_NAME = "테스트 기본 메뉴그룹명";

    private MenuGroupHelper() {
    }

    public static MenuGroup create() {
        return create(DEFAULT_NAME);
    }

    public static MenuGroup create(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

}
