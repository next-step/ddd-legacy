package kitchenpos.helper;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static final MenuGroup CHICKEN = create("치킨");

    public static final MenuGroup PIZZA = create("피자");

    private static MenuGroup create(String name) {
        var menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup request(String name) {
        var menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}
