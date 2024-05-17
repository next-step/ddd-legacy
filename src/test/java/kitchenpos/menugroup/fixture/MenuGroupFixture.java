package kitchenpos.menugroup.fixture;

import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static final MenuGroup A_메뉴그룹 = 메뉴그룹을_생성하다("A");

    public static final MenuGroup B_메뉴그룹 = 메뉴그룹을_생성하다("B");

    public static final MenuGroup 이름미존재_메뉴그룹 = 메뉴그룹을_생성하다(null);

    public static final MenuGroup 빈문자이름_메뉴그룹 = 메뉴그룹을_생성하다("");

    private static MenuGroup 메뉴그룹을_생성하다(String name) {
        var 메뉴그룹 = new MenuGroup();
        메뉴그룹.setName(name);

        return 메뉴그룹;
    }

}
