package kitchenpos.menu.supports;

import java.util.List;
import java.util.Optional;

import kitchenpos.dao.MenuDao;
import kitchenpos.model.Menu;

public class MenuDaoWithConstraint implements MenuDao {

    public static final IllegalArgumentException MENU_CONSTRAINT_EXCEPTION = new IllegalArgumentException() {};
    private final MenuDao delegate;

    public static MenuDaoWithConstraint withCollection(List<Menu> entities) {
        return new MenuDaoWithConstraint(new MenuDaoWithCollection(entities));
    }

    public MenuDaoWithConstraint(MenuDao delegate) {
        this.delegate = delegate;
    }

    @Override
    public Menu save(Menu entity) {
        if (entity.getName() == null) {
            throw MENU_CONSTRAINT_EXCEPTION;
        }
        return delegate.save(entity);
    }

    @Override
    public Optional<Menu> findById(Long id) {return delegate.findById(id);}

    @Override
    public List<Menu> findAll() {return delegate.findAll();}

    @Override
    public long countByIdIn(List<Long> ids) {return delegate.countByIdIn(ids);}
}
