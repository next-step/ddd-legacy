package kitchenpos.unit.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {
    public static final MenuGroup 탕수육_세트;

    static {
        탕수육_세트 = createMenuGroup("d9bc21ac-cc10-4593-b506-4a40e0170e02", "탕수육 세트");
    }

    private static MenuGroup createMenuGroup(String id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.fromString(id));
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup createMenuGroup(String name) {
        return createMenuGroup("", name);
    }
}
