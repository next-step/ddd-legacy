package kitchenpos.dao;

import kitchenpos.model.MenuProduct;

import java.util.*;
import java.util.stream.Collectors;

public class TestMenuProductDao implements MenuProductDao {

    private final Map<Long, MenuProduct> menuProducts = new HashMap();

    @Override
    public MenuProduct save(MenuProduct entity) {
        menuProducts.put(entity.getProductId(), entity);
        return entity;
    }

    @Override
    public Optional<MenuProduct> findById(Long id) {
        return Optional.ofNullable(menuProducts.get(id));
    }

    @Override
    public List<MenuProduct> findAll() {
        return new ArrayList(menuProducts.values());
    }

    @Override
    public List<MenuProduct> findAllByMenuId(Long menuId) {
        return menuProducts.values()
                .stream()
                .filter(menuProduct -> menuId.equals(menuProduct.getMenuId()))
                .collect(Collectors.toList());
    }
}
