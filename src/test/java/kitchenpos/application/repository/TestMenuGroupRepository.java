package kitchenpos.application.repository;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestMenuGroupRepository implements MenuGroupRepository {
    private final List<MenuGroup> menuGroups = new ArrayList<>();

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
