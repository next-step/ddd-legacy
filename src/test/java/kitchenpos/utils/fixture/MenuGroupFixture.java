package kitchenpos.utils.fixture;

import kitchenpos.application.InMemoryMenuGroupRepository;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import static java.util.UUID.randomUUID;

public class MenuGroupFixture {

    public static MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    public static MenuGroup 메뉴그룹() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("메뉴그룹 이름");
        return menuGroup;
    }

    public static MenuGroup 메뉴그룹저장() {
        final MenuGroup menuGroup = 메뉴그룹();
        menuGroup.setId(randomUUID());
        return menuGroupRepository.save(menuGroup);
    }

    public static void 비우기() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
    }
}
