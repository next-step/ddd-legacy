package kitchenpos.menu.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {
    public static final MenuGroup 메뉴_그룹_한식 = MenuGroupFixture.create(UUID.randomUUID(), "한식");
    public static final MenuGroup 메뉴_그룹_중식 = MenuGroupFixture.create(UUID.randomUUID(), "중식");
    public static final MenuGroup 메뉴_그룹_양식 = MenuGroupFixture.create(UUID.randomUUID(), "양식");
    public static final MenuGroup 메뉴_그룹_일식 = MenuGroupFixture.create(UUID.randomUUID(), "일식");

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
