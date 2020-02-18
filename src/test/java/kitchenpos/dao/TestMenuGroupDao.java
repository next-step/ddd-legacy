package kitchenpos.dao;

import kitchenpos.model.MenuGroup;

import java.util.*;

public class TestMenuGroupDao implements MenuGroupDao {

    private final Map<Long, MenuGroup> menuGroups = new HashMap();

    @Override
    public MenuGroup save(MenuGroup entity) {
        menuGroups.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<MenuGroup> findById(Long id) {
        return Optional.ofNullable(menuGroups.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList(menuGroups.values());
    }

    @Override
    public boolean existsById(Long id) {
        return menuGroups.containsKey(id);
    }
}
