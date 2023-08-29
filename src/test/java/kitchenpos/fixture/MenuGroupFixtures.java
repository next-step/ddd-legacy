package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixtures {
    public static MenuGroup 메뉴_그룹_등록() {
        return 메뉴_그룹_등록("등심세트");
    }

    public static MenuGroup 메뉴_그룹_등록(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }
}
