package kitchenpos.dao;

import kitchenpos.model.MenuProduct;
import kitchenpos.support.MenuProductBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMenuProductDao implements MenuProductDao{
    final Map<Long, MenuProduct> entities = new HashMap<>();

    @Override
    public MenuProduct save(MenuProduct entity) {
        MenuProduct menuProduct = new MenuProductBuilder()
            .seq(entity.getSeq())
            .menuId(entity.getMenuId())
            .productId(entity.getProductId())
            .build();
        return menuProduct;
    }

    @Override
    public Optional<MenuProduct> findById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<MenuProduct> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public List<MenuProduct> findAllByMenuId(Long menuId) {
        return this.findAll().stream()
            .filter(menuProduct -> menuProduct.getMenuId() == menuId)
            .collect(Collectors.toList());
    }
}
