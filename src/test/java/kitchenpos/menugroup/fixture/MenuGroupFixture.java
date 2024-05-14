package kitchenpos.menugroup.fixture;

import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static final MenuGroup A_메뉴그룹 = A_메뉴그룹_생성();
    private static MenuGroup A_메뉴그룹_생성() {
        var 메뉴그룹 = new MenuGroup();
        메뉴그룹.setName("A");

        return 메뉴그룹;
    }

    public static final MenuGroup 이름미존재_메뉴그룹 = 이름미존재_메뉴그룹_생성();
    private static MenuGroup 이름미존재_메뉴그룹_생성() {
        return new MenuGroup();
    }

    public static final MenuGroup 빈문자이름_메뉴그룹 = 빈문자이름_메뉴그룹_생성();
    private static MenuGroup 빈문자이름_메뉴그룹_생성() {
        var 메뉴그룹 = new MenuGroup();
        메뉴그룹.setName("");

        return 메뉴그룹;
    }

}
