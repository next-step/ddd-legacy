package kitchenpos.fake;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeMenuGroupRepository implements MenuGroupRepository {

    private final Map<UUID, MenuGroup> menuGroups = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        this.menuGroups.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }

    @Override
    public Optional<MenuGroup> findById(UUID uuid) {
        return Optional.ofNullable(menuGroups.get(uuid));
    }

    @Override
    public List<MenuGroup> findAll() {
        return menuGroups.values()
                .stream()
                .collect(Collectors.toList());
    }
}
