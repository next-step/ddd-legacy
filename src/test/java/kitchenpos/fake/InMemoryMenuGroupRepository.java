package kitchenpos.fake;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {
    private final HashMap<UUID, MenuGroup> entities = new HashMap<>();


    @Override
    public MenuGroup save(MenuGroup entity) {
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<MenuGroup> findById(UUID uuid) {
        return Optional.ofNullable(entities.get(uuid));
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public List<MenuGroup> findAllByIdIn(List<UUID> ids) {
        return entities.values().stream().filter(menuGroup -> ids.contains(menuGroup.getId())).collect(Collectors.toList());
    }
}