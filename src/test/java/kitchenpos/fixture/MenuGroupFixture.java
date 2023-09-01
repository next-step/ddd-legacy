package kitchenpos.fixture;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static final String DEFAULT_GROUP_NAME = "기본 그룹";

    public static MenuGroup create(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup createDefault() {
        return create(DEFAULT_GROUP_NAME);
    }

}
