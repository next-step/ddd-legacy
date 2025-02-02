package kitchenpos;

import helper.IdGenerator;
import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    private static final IdGenerator menuGroupIdGenerator = UUID::randomUUID;

    public static MenuGroup 추천_메뉴그룹() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupIdGenerator.ramdom());
        menuGroup.setName("추천 메뉴");
        return menuGroup;
    }

    public static MenuGroup 신_메뉴그룹() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupIdGenerator.ramdom());
        menuGroup.setName("신 메뉴");
        return menuGroup;
    }

    public static MenuGroup 요청_메뉴그룹(final String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }

}
