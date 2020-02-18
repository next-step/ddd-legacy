package kitchenpos.dao;

import kitchenpos.model.Menu;

import java.util.*;

public class InMemoryMenuDao implements DefaultMenuDao {
    private Map<Long, Menu> data = new HashMap<>();

    @Override
    public Menu save(Menu entity) {
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Menu> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public long countByIdIn(List<Long> ids) {
        return ids.stream().filter(id -> data.containsKey(id)).count();
    }
}