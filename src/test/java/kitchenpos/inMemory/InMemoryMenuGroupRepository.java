package kitchenpos.inMemory;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {

    private Map<UUID, MenuGroup> menuGroups = new HashMap<>();

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList(menuGroups.values());
    }

    @Override
    public MenuGroup save(final MenuGroup menuGroup) {
        menuGroups.put(UUID.randomUUID(), menuGroup);
        return menuGroup;
    }

    @Override
    public Optional<MenuGroup> findById(final UUID menuGroupId) {
        return menuGroups.values()
                .stream()
                .filter(it -> Objects.equals(it.getId(), menuGroupId))
                .findAny();
    }
}
