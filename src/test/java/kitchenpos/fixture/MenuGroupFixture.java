package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static final String DEFAULT_NAME = "기본 메뉴 그룹";

    private MenuGroupFixture() {
    }

    public static MenuGroup create() {
        return create(DEFAULT_NAME);
    }
    public static MenuGroup create(String name) {
        MenuGroup result = new MenuGroup();
        result.setName(name);
        return result;
    }
}
