package kitchenpos.service;

import java.util.UUID;

import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    private final MenuGroup menuGroup;

    public MenuGroupFixture() {
        this.menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("추천 메뉴");
    }

    public static MenuGroupFixture builder() {
        return new MenuGroupFixture();
    }

    public MenuGroupFixture name(String name) {
        menuGroup.setName(name);
        return this;
    }


    public MenuGroup build() {
        return menuGroup;
    }
}
