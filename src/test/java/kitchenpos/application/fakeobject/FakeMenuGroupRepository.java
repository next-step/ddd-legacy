package kitchenpos.application.fakeobject;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakeMenuGroupRepository implements MenuGroupRepository {
    private List<MenuGroup> menuGroups;

    public FakeMenuGroupRepository() {
        this.menuGroups = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            menuGroups.add(MenuGroup.of("test" + i));
        }
    }

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        menuGroup.setId(UUID.randomUUID());
        menuGroups.add(menuGroup);
        return menuGroup;
    }

    @Override
    public List<MenuGroup> findAll() {
        return menuGroups;
    }
}
