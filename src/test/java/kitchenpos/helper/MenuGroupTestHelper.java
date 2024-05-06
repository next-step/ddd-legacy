package kitchenpos.helper;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupTestHelper {
    public static MenuGroup 메뉴카테고리_생성(String name){
        UUID id = UUID.randomUUID();

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);

        return menuGroup;
    }
}
