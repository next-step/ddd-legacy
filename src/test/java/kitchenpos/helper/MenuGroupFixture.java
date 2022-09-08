package kitchenpos.helper;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static MenuGroup create(String name) {
        var menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }
}
