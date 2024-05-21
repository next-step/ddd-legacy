package kitchenpos.application;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    public MenuGroupFixture() {
    }
    public static MenuGroup createMenuGroupRequest() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("한마리메뉴");
        return createMenuGroupRequest(menuGroup.getName());
    }

    public static MenuGroup createMenuGroupRequest(final String name) {
        final MenuGroup requestMenuGroup = new MenuGroup();
        requestMenuGroup.setId(UUID.randomUUID());
        requestMenuGroup.setName(name);
        return requestMenuGroup;
    }



}
