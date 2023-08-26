package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static final String DEFAULT_NAME = "기본 메뉴 그룹";

    private MenuGroupFixture() {
    }

    public static MenuGroup create() {
        MenuGroup result = new MenuGroup();
        result.setName(DEFAULT_NAME);
        return result;
    }
}
