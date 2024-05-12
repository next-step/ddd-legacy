package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {
    private MenuGroupFixture() {

    }

    public static MenuGroup createMenuGroupWithId(String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);

        return menuGroup;
    }

    public static MenuGroup createMenuGroupWithId(UUID id, String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);

        return menuGroup;
    }
}
