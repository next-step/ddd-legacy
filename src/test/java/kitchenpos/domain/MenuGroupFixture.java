package kitchenpos.domain;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MenuGroupFixture {
    private MenuGroupFixture() {}

    private static MenuGroup createMenuGroup(final String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    private static List<MenuGroup> createMenuGroups() {
        return Arrays.asList(
                createMenuGroup("첫번째 그룹"),
                createMenuGroup("두번째 그룹"),
                createMenuGroup("세번째 그룹"),
                createMenuGroup("네번째 그룹")
        );
    }

    public static MenuGroupRepository createMenuGroupRepository() {
        final MenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();
        createMenuGroups().forEach(menuGroupRepository::save);
        return menuGroupRepository;
    }
}
