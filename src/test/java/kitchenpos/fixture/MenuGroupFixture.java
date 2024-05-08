package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {
    public static final String NAME_한마리메뉴 = "한마리메뉴";
    public static final String NAME_추천메뉴 = "추천메뉴";

    public static MenuGroup menuGroupCreateRequest(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup menuGroupResponse(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }
}
