package kitchenpos.testfixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupTestFixture {

    public static MenuGroup createMenuGroupRequest() {
        return createMenuGroupRequest("치킨류");
    }

    public static MenuGroup createMenuGroupRequest(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);

        return menuGroup;

    }

    public static MenuGroup createMenuGroup(UUID id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);

        return menuGroup;
    }
}
