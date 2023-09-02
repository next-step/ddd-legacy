package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    public static final String DEFAULT_NAME = "기본 메뉴 그룹";

    private MenuGroupFixture() {
    }

    public static MenuGroup create() {
        return create(UUID.randomUUID(), DEFAULT_NAME);
    }
    public static MenuGroup create(UUID id, String name) {
        MenuGroup result = new MenuGroup();
        result.setId(id);
        result.setName(name);
        return result;
    }
}
