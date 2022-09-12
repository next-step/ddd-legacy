package kitchenpos.fixture.request;

import kitchenpos.domain.MenuGroup;

public class MenuGroupRequestFixture {

    public static MenuGroup createRequest(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}
