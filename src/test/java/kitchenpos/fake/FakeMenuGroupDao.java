package kitchenpos.fake;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;

import java.util.*;

public class FakeMenuGroupDao implements MenuGroupDao {
    private Map<Long, MenuGroup> entities = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup entity) {
        entities.put(entity.getId(), entity);
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
