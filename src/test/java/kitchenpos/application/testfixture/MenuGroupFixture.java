package kitchenpos.application.testfixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public record MenuGroupFixture() {

    public static MenuGroup newOne() {
        var menuGroup = new MenuGroup();
        menuGroup.setName("신메뉴");
        return menuGroup;
    }

    public static MenuGroup newOne(String name) {
        var menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }
}
