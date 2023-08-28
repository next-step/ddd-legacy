package kitchenpos.objectmother;

import kitchenpos.domain.MenuGroup;

public class MenuGroupMaker {

    public static final MenuGroup 메뉴그룹_이름없음 = make();
    public static final MenuGroup 메뉴그룹_1 = make("메뉴그룹1");
    public static final MenuGroup 메뉴그룹_2 = make("메뉴그룹2");

    public static MenuGroup make() {
        return new MenuGroup();
    }

    public static MenuGroup make(String name) {
        return new MenuGroup(name);
    }

}
