package kitchenpos.unit.fixture;

import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {
    public static final MenuGroup 탕수육_세트;

    static {
        탕수육_세트 = createMenuGroup("탕수육 세트");
    }

    public static MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}
