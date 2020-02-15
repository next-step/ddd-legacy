package kitchenpos.menu.group.supports;

import java.util.List;
import java.util.Optional;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;

public class MenuGroupDaoWithConstraint implements MenuGroupDao {

    public static final IllegalArgumentException MENU_GROUP_CONSTRAINT_EXCEPTION = new IllegalArgumentException() {};
    private final MenuGroupDao delegate;

    public static MenuGroupDaoWithConstraint withCollection() {
        return new MenuGroupDaoWithConstraint(new MenuGroupDaoWithCollection());
    }

    public MenuGroupDaoWithConstraint(MenuGroupDao delegate) {
        this.delegate = delegate;
    }

    @Override
    public MenuGroup save(MenuGroup entity) {
        if (entity.getName() == null) {
            throw MENU_GROUP_CONSTRAINT_EXCEPTION;
        }
        return delegate.save(entity);
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
