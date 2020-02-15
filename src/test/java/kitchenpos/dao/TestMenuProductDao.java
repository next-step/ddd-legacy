package kitchenpos.dao;

import kitchenpos.model.MenuProduct;

import java.util.*;
import java.util.stream.Collectors;

public class TestMenuProductDao implements MenuProductDao {

    private Map<Long, MenuProduct> menuProducts = new HashMap<>();

    @Override
    public MenuProduct save(MenuProduct entity) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(entity.getProductId());

        menuProducts.put(entity.getProductId(), entity);
        return entity;
    }

    @Override
    public Optional<MenuProduct> findById(Long id) {
        Objects.requireNonNull(id);

        return Optional.ofNullable(menuProducts.get(id));
    }

    @Override
    public List<MenuProduct> findAll() {
        return new ArrayList<MenuProduct>(menuProducts.values());
    }

    @Override
    public List<MenuProduct> findAllByMenuId(Long menuId) {
        Objects.requireNonNull(menuId);

        return menuProducts.values()
                .stream()
                .filter(i -> i.getMenuId().equals(menuId))
                .collect(Collectors.toList());
    }
}
