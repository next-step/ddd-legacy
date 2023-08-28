package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    public static MenuGroup TEST_MENU_GROUP() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("테스트 메뉴 그룹");
        menuGroup.setId(UUID.randomUUID());
        return menuGroup;
    }
}
