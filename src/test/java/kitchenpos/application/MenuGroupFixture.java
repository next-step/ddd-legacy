package kitchenpos.application;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static final MenuGroup 세트메뉴 = new MenuGroup();
    public static final MenuGroup 추천메뉴 = new MenuGroup();
    public static final List<MenuGroup> 메뉴판 = Arrays.asList(세트메뉴, 추천메뉴);

    static {
        initialize(세트메뉴, "세트메뉴");
        initialize(추천메뉴, "추천메뉴");
    }

    private static void initialize(MenuGroup menuGroup, String name) {
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
    }
}
