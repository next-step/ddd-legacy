package kitchenpos.application.fixture;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public record MenuGroupFixture(UUID id, String 메뉴그룹명) {

    public static final String DEFAULT_MENU_GROUP_NAME = "치킨";

    public static MenuGroupFixture init() {
        return new MenuGroupFixture(UUID.randomUUID(), DEFAULT_MENU_GROUP_NAME);
    }

    public static MenuGroupFixture test(String 메뉴그룹명) {
        return new MenuGroupFixture(
            UUID.randomUUID(),
            메뉴그룹명
        );
    }

    public MenuGroup create() {
        var menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(메뉴그룹명);

        return menuGroup;
    }
}

