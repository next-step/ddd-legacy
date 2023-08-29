package kitchenpos.fixture;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    private MenuGroupFixture() {
    }

    public static MenuGroup create(
        UUID id,
        String name
    ) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup create(
        String name
    ) {
        return create(UUID.randomUUID(), name);
    }
}
