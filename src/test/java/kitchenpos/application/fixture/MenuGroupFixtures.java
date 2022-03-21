package kitchenpos.application.fixture;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public final class MenuGroupFixtures {

    private MenuGroupFixtures() {
        throw new RuntimeException("생성할 수 없는 클래스");
    }

    public static MenuGroup createMenuGroup(UUID id, String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup createMenuGroup(String name) {
        return createMenuGroup(UUID.randomUUID(), name);
    }
}
