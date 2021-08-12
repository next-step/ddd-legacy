package kitchenpos.menu_group.fixture;

import kitchenpos.domain.MenuGroup;
import org.springframework.test.util.ReflectionTestUtils;

public class MenuGroupFixture {

    public static MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        ReflectionTestUtils.setField(menuGroup, "name", name);
        return menuGroup;
    }
}
