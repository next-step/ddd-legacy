package kitchenpos.fake.menuGroup;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TestMenuGroupRepository implements MenuGroupRepository {
    private final ConcurrentHashMap<UUID, MenuGroup> menuGroups = new ConcurrentHashMap<>();

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return Optional.ofNullable(menuGroups.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroups.values());
    }

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        menuGroups.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }
}
