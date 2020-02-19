package kitchenpos.menu.group.supports;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;

public class MenuGroupDaoWithCollection implements MenuGroupDao {

    private long id = 0;
    private final Map<Long, MenuGroup> entities;

    public MenuGroupDaoWithCollection(List<MenuGroup> entities) {
        this.entities = entities.stream()
                                .peek(e -> e.setId(++id))
                                .collect(Collectors.toMap(MenuGroup::getId,
                                                          Function.identity()));
    }

    @Override
    public MenuGroup save(MenuGroup entity) {
        if (entity.getId() == null) { entity.setId(++id); }
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<MenuGroup> findById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return null;
    }

    @Override
    public boolean existsById(Long id) {
        return entities.containsKey(id);
    }
}
