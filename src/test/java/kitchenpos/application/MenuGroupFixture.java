package kitchenpos.application;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static final MenuGroup 세트메뉴 = new MenuGroup();
    public static final MenuGroup 추천메뉴 = new MenuGroup();

    static {
        initialize(세트메뉴, "세트메뉴");
        initialize(추천메뉴, "추천메뉴");
    }

    private static void initialize(MenuGroup menuGroup, String name) {
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
    }
}
