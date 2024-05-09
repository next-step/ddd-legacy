package kitchenpos.menu.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {
    public static final MenuGroup 메뉴_그룹_A = MenuGroupFixture.create(UUID.randomUUID(), "그룹A");
    public static final MenuGroup 메뉴_그룹_B = MenuGroupFixture.create(UUID.randomUUID(), "그룹B");
    public static final MenuGroup 메뉴_그룹_C = MenuGroupFixture.create(UUID.randomUUID(), "그룹C");
    public static final MenuGroup 메뉴_그룹_D = MenuGroupFixture.create(UUID.randomUUID(), "그룹D");

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
