package kitchenpos.menugroup;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {
    public static MenuGroup menuGroup() {
        return new MenuGroup(UUID.randomUUID(), "추천메뉴");
    }

    public static MenuGroup menuGroup(String name) {
        return new MenuGroup(name);
    }
}
