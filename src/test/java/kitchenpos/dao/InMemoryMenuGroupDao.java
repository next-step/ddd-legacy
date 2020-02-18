package kitchenpos.dao;

import kitchenpos.model.MenuGroup;

import java.util.*;

public class InMemoryMenuGroupDao implements DefaultMenuGroupDao {

    private Map<Long, MenuGroup> data = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup entity) {
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<MenuGroup> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean existsById(Long id) {
        return data.containsKey(id);
    }
}
