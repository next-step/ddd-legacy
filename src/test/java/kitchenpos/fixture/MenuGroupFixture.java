package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static MenuGroup.Builder aMenuGroupRequest() {
        return MenuGroup.builder()
                .name("메뉴그룹");
    }
}
