package kitchenpos.menu.group.supports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;

public class MenuGroupDaoWithCollection implements MenuGroupDao {

    private Map<Long, MenuGroup> menuGroups = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup entity) {
        menuGroups.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<MenuGroup> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<MenuGroup> findAll() {
        return null;
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }
}
