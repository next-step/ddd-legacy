package kitchenpos.domain;

import java.util.*;

public class FakeMenuGroupRepository implements MenuGroupRepository {
    private Map<UUID, MenuGroup> map = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        map.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return null;
    }
}
