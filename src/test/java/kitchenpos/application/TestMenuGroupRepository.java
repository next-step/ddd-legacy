package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;

public class TestMenuGroupRepository implements MenuGroupRepository {
    private final Set<MenuGroup> menuGroups = new HashSet<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        if (menuGroup.getId() == null) {
            menuGroup.setId(UUID.randomUUID());
        }
        menuGroups.add(menuGroup);
        return menuGroup;
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroups);
    }

    @Override
    public Optional<MenuGroup> findById(UUID menuGroupId) {
        return menuGroups.stream()
                .filter(menuGroup -> menuGroup.getId().equals(menuGroupId))
                .findAny();
    }
}
