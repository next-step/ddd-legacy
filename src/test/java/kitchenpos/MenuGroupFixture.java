package kitchenpos;

import kitchenpos.infra.IdGenerator;
import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    private static final IdGenerator menuGroupIdGenerator = UUID::randomUUID;

    public static MenuGroup 추천_메뉴그룹_Request() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("추천 메뉴");
        return menuGroup;
    }

    public static MenuGroup 메뉴그룹_Request(final String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }

}
