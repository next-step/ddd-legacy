package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    public static MenuGroup create() {
        return create(UUID.randomUUID(), "치킨");
    }

    public static MenuGroup create(final String name) {
        return create(UUID.randomUUID(), name);
    }

    private static MenuGroup create(final UUID id, final String name) {
        final MenuGroup menuGroup = new MenuGroup();

        menuGroup.setName(name);
        menuGroup.setId(id);

        return menuGroup;
    }

}
