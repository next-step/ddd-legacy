package kitchenpos.mock;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;

public class MockMenuGroupRepository implements MenuGroupRepository {
    private final Map<UUID, MenuGroup> menuGroupMap = new HashMap<>();

    @Override
    public MenuGroup save(final MenuGroup menuGroup) {
        menuGroupMap.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroupMap.values());
    }

    @Override
    public Optional<MenuGroup> findById(final UUID menuGroupId) {
        return Optional.ofNullable(menuGroupMap.get(menuGroupId));
    }
}
