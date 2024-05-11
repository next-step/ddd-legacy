package kitchenpos.menu.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {
    public MenuGroup 메뉴_그룹_A = MenuGroupFixture.create(UUID.randomUUID(), "그룹A");

    public static MenuGroup create(String name) {
        return create(UUID.randomUUID(), name);
    }

    public static MenuGroup create(UUID id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);

        return menuGroup;
    }
}
