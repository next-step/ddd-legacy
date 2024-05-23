package kitchenpos.menugroup.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    public static final MenuGroup A_메뉴그룹 = 메뉴그룹을_생성한다("A");

    public static final MenuGroup B_메뉴그룹 = 메뉴그룹을_생성한다("B");

    public static final MenuGroup 이름미존재_메뉴그룹 = 메뉴그룹을_생성한다(null);

    public static final MenuGroup 빈문자이름_메뉴그룹 = 메뉴그룹을_생성한다("");

    public static final MenuGroup C_메뉴그룹 = 메뉴그룹을_생성한다(UUID.randomUUID(), "C");

    private static MenuGroup 메뉴그룹을_생성한다(String name) {
        return 메뉴그룹을_생성한다(null, name);
    }

    private static MenuGroup 메뉴그룹을_생성한다(UUID id, String name) {
        var 메뉴그룹 = new MenuGroup();
        메뉴그룹.setId(id);
        메뉴그룹.setName(name);

        return 메뉴그룹;
    }

}
