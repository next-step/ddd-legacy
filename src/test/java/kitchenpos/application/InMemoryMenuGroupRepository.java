package kitchenpos.application;

import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;

import java.util.*;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {
    private final Map<UUID, MenuGroup> menuGroups = new HashMap<>();

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroups.values());
    }

    @Override
    public MenuGroup save(final MenuGroup menuGroup) {
        menuGroups.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }
}
