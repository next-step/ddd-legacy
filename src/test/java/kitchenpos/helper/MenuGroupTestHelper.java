package kitchenpos.helper;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.UUID;

public class MenuGroupTestHelper {
    private static MenuGroupRepository menuGroupRepository;

    public MenuGroupTestHelper(MenuGroupRepository menuGroupRepository) {
        this.menuGroupRepository = menuGroupRepository;
    }

    public static MenuGroup 메뉴카테고리_생성(String name){
        UUID id = UUID.randomUUID();

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);

        return menuGroupRepository.save(menuGroup);
    }
}
