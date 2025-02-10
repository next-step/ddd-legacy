package kitchenpos.application.fixture;

import kitchenpos.domain.MenuGroup;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

public class MenuGroupFixture {

    public static MenuGroup createMenuGroup(String name) {
        return createMenuGroup(null, name);
    }

    public static MenuGroup createMenuGroup(UUID id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        ReflectionTestUtils.setField(menuGroup, "id", id);
        ReflectionTestUtils.setField(menuGroup, "name", name);
        return menuGroup;
    }

    private MenuGroupFixture() {
    }
}
