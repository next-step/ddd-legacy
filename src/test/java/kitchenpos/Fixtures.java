package kitchenpos;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class Fixtures {

    public static MenuGroup 메뉴그룹_생성(final String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

}
