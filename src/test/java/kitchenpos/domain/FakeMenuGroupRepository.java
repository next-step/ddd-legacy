package kitchenpos.domain;

import java.util.*;

public class FakeMenuGroupRepository implements MenuGroupRepository {
    Map<UUID, MenuGroup> menuGroupMap = new LinkedHashMap<>();

    @Override
    public MenuGroup save(final MenuGroup menuGroup) {
        menuGroupMap.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }

    @Override
    public Optional<MenuGroup> findById(final UUID menuGroupId) {
        return Optional.ofNullable(menuGroupMap.get(menuGroupId));
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroupMap.values());
    }
}
