package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {
    private final Map<UUID, MenuGroup> menuGroups = new ConcurrentHashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        menuGroups.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return Optional.ofNullable(menuGroups.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroups.values());
    }
}
