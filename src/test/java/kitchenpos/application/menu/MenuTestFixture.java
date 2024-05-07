package kitchenpos.application.menu;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuTestFixture {

    public static MenuGroup aMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("레허순살반반");
        return menuGroup;
    }

}
