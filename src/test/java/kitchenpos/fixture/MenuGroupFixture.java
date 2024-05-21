package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MenuGroupFixture {

    private static final String 메뉴그룹명 = "메뉴 그룹명";

    public static @NotNull MenuGroup createMenuGroup() {
        return createMenuGroup(메뉴그룹명);
    }

    public static @NotNull MenuGroup createMenuGroup(String name) {
        final var menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }
}
