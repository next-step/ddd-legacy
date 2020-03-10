package kitchenpos.fake;

import kitchenpos.dao.MenuDao;
import kitchenpos.model.Menu;

import java.util.*;

public class FakeMenuDao implements MenuDao {
    private Map<Long, Menu> entities = new HashMap<>();

    @Override
    public Menu save(Menu entity) {
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Menu> findById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public long countByIdIn(List<Long> ids) {
        return entities.values()
                .stream()
                .filter(menu -> ids.contains(menu.getId()))
                .count();
    }
}
