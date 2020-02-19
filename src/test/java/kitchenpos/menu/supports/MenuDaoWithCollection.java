package kitchenpos.menu.supports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import kitchenpos.dao.MenuDao;
import kitchenpos.model.Menu;

public class MenuDaoWithCollection implements MenuDao {

    private long id = 0;
    private final Map<Long, Menu> entities;

    public MenuDaoWithCollection() {
        this.entities = new HashMap<>();
    }

    public MenuDaoWithCollection(List<Menu> entities) {
        this.entities = entities.stream()
                                .peek(e -> e.setId(++id))
                                .collect(Collectors.toMap(Menu::getId,
                                                          Function.identity()));
    }

    @Override
    public Menu save(Menu entity) {
        if (entity.getId() == null) { entity.setId(++id); }
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Menu> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Menu> findAll() {
        return null;
    }

    @Override
    public long countByIdIn(List<Long> ids) {
        return ids.stream()
                  .filter(entities::containsKey)
                  .distinct()
                  .count();
    }
}
