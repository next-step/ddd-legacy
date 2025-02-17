package kitchenpos.domain;

import java.util.*;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {

    private Map<UUID, MenuGroup> menuGroups = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        final var id = UUID.randomUUID();
        menuGroup.setId(id);
        menuGroups.put(id, menuGroup);
        return menuGroup;
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroups.values());
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return Optional.ofNullable(menuGroups.get(id));
    }
}
