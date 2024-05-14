package kitchenpos.fixture;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static MenuGroup createRequest(final String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}
