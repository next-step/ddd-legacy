package kitchenpos.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class FakeMenuGroupRepository implements MenuGroupRepository {

    private final Map<UUID, MenuGroup> menuGroups = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        this.menuGroups.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }

    @Override
    public List<MenuGroup> findAll() {
        final Collection<MenuGroup> results = this.menuGroups.values();
        return List.copyOf(results);
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        final MenuGroup result = this.menuGroups.get(id);
        return Optional.ofNullable(result);
    }
}
