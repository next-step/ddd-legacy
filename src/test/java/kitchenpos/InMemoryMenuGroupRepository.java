package kitchenpos;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {
    private final Map<UUID, MenuGroup> menuGroups = new HashMap<>();

    @Override
    public Optional<MenuGroup> findById(UUID menuGroupId) {
        return Optional.ofNullable(menuGroups.get(menuGroupId));
    }

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        if (Objects.isNull(menuGroup.getId())) {
            menuGroup.setId(UUID.randomUUID());
            menuGroups.put(menuGroup.getId(), menuGroup);
        } else {
            menuGroups.put(menuGroup.getId(), menuGroup);
        }
        return menuGroup;
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroups.values());
    }
}
