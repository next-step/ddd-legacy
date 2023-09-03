package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static MenuGroup 주류() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("주류");
        return menuGroup;
    }

    public static MenuGroup 한마리() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("한마리");
        return menuGroup;
    }

    public static MenuGroup 비어있음() {
        return new MenuGroup();
    }

    public static MenuGroup 반반() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("반반");
        return menuGroup;
    }
}

