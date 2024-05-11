package kitchenpos.application.testFixture;

import kitchenpos.domain.MenuGroup;

public record MenuGroupFixture() {

    public static MenuGroup newOne() {
        var menuGroup = new MenuGroup();
        menuGroup.setName("신메뉴");
        return menuGroup;
    }

    public static MenuGroup newOne(String name) {
        var menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}
