package kitchenpos.dao;

import kitchenpos.model.MenuGroup;

import java.util.*;

public class TestMenuGroupDao implements MenuGroupDao {

    private Map<Long, MenuGroup> menuGroups = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup entity) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(entity.getId());

        menuGroups.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<MenuGroup> findById(Long id) {

        return Optional.ofNullable(menuGroups.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<MenuGroup>(menuGroups.values());
    }

    @Override
    public boolean existsById(Long id) {
        return Objects.nonNull(menuGroups.get(id));
    }
}
