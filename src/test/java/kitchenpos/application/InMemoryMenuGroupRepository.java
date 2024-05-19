package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;

public class InMemoryMenuGroupRepository implements MenuGroupRepository{

    private final Map<UUID, MenuGroup> menuGroups = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup entity) {
        menuGroups.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return Optional.ofNullable(menuGroups.get(id));

    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroups.values());
    }

    @Override
    public List<MenuGroup> findAllByIdIn(List<UUID> ids) {
        return menuGroups.values()
                .stream()
                .filter(menuGroup -> ids.contains(menuGroup.getId()))
                .toList();
    }
}
