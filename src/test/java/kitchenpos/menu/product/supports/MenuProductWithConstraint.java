package kitchenpos.menu.product.supports;

import java.util.List;
import java.util.Optional;

import kitchenpos.dao.MenuProductDao;
import kitchenpos.model.MenuProduct;

public class MenuProductWithConstraint implements MenuProductDao {

    private final MenuProductDao delegate;

    public static MenuProductWithConstraint withCollection(List<MenuProduct> entities) {
        return new MenuProductWithConstraint(new MenuProductWithCollection(entities));
    }

    public MenuProductWithConstraint(MenuProductDao delegate) {
        this.delegate = delegate;
    }

    @Override
    public MenuProduct save(MenuProduct entity) {return delegate.save(entity);}

    @Override
    public Optional<MenuProduct> findById(Long id) {return delegate.findById(id);}

    @Override
    public List<MenuProduct> findAll() {return delegate.findAll();}

    @Override
    public List<MenuProduct> findAllByMenuId(Long menuId) {return delegate.findAllByMenuId(menuId);}
}
