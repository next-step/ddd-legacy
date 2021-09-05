package kitchenpos.utils.fixture;

import kitchenpos.domain.MenuGroup;

import static java.util.UUID.randomUUID;

public class MenuGroupFixture {

    public static MenuGroup 메뉴그룹() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(randomUUID());
        menuGroup.setName("메뉴그룹 이름");
        return menuGroup;
    }
}
