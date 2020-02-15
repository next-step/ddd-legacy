package kitchenpos.dao;

import java.util.List;
import java.util.Optional;

import kitchenpos.model.MenuProduct;

public interface MenuProductDao {
    MenuProduct save(MenuProduct entity);

    Optional<MenuProduct> findById(Long id);

    List<MenuProduct> findAll();

    List<MenuProduct> findAllByMenuId(Long menuId);
}
