package kitchenpos.dao;

import kitchenpos.model.MenuGroup;
import kitchenpos.support.MenuGroupBuilder;

import java.util.*;

public class InMemoryMenuGroupDao implements MenuGroupDao {

    private final Map<Long, MenuGroup> entities = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup entity) {
        MenuGroup savedMenuGroup = new MenuGroupBuilder()
            .id(entity.getId())
            .name(entity.getName())
            .build();

        entities.put(entity.getId(), savedMenuGroup);
        return entity;
    }

    @Override
    public Optional<MenuGroup> findById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public boolean existsById(Long id) {
        return entities.containsKey(id);
    }
}
