package kitchenpos.fixture.request;

import kitchenpos.domain.MenuGroup;

public class MenuGroupRequestFixture {

    public static MenuGroup createMenuGroupRequest(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}
