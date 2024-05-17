package kitchenpos.infra;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {

    private final Map<UUID, MenuGroup> menuGroups = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        menuGroups.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }

    @Override
    public List<MenuGroup> findAll() {
        return menuGroups.values().stream().toList();
    }

    @Override
    public Optional<MenuGroup> findById(UUID menuGroupId) {
        return Optional.ofNullable(menuGroups.get(menuGroupId));
    }
}
