package kitchenpos.test_fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupTestFixture {

    private MenuGroup menuGroup;

    private MenuGroupTestFixture(MenuGroup menuGroup) {
        this.menuGroup = menuGroup;
    }

    public static MenuGroupTestFixture create() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("테스트 메뉴 그룹");
        return new MenuGroupTestFixture(menuGroup);
    }

    public MenuGroupTestFixture changeId(UUID id) {
        MenuGroup newMenuGroup = new MenuGroup();
        newMenuGroup.setId(id);
        newMenuGroup.setName(menuGroup.getName());
        this.menuGroup = newMenuGroup;
        return this;
    }

    public MenuGroupTestFixture changeName(String name) {
        MenuGroup newMenuGroup = new MenuGroup();
        newMenuGroup.setId(menuGroup.getId());
        newMenuGroup.setName(name);
        this.menuGroup = newMenuGroup;
        return this;
    }

    public MenuGroup getMenuGroup() {
        return this.menuGroup;
    }
}
