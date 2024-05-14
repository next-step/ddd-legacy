package kitchenpos.fixture;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static MenuGroup createRequest(final String name) {
        return createRequest(UUID.randomUUID(), name);
    }

    public static MenuGroup createRequest(final UUID id, final String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

}
