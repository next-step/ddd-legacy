package kitchenpos.fixtures;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    private final MenuGroup menuGroup;

    public MenuGroupFixture(final String name) {
        this.menuGroup = this.ofFixture(name);
    }

    public MenuGroup ofFixture(final String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public MenuGroup getMenuGroup() {
        return menuGroup;
    }
}
