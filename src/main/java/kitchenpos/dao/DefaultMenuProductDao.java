package kitchenpos.dao;

import kitchenpos.model.MenuProduct;

import java.util.List;
import java.util.Optional;

public interface DefaultMenuProductDao {
    MenuProduct save(MenuProduct entity);

    Optional<MenuProduct> findById(Long id);

    List<MenuProduct> findAll();

    List<MenuProduct> findAllByMenuId(Long menuId);
}
