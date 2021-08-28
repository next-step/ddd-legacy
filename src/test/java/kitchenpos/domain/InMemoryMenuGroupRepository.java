package kitchenpos.domain;

import java.util.*;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {
    private final Map<UUID, MenuGroup> menuGroups;

    public InMemoryMenuGroupRepository() {
        menuGroups = new HashMap<>();
    }

    @Override
    public Optional<MenuGroup> findById(UUID uuid) {
        return Optional.ofNullable(menuGroups.get(uuid));
    }

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        menuGroup.setId(UUID.randomUUID());
        menuGroups.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroups.values());
    }
}
