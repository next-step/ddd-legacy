package kitchenpos.dao;

import kitchenpos.model.MenuProduct;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMenuProductDao implements DefaultMenuProductDao {
    private Map<Long, MenuProduct> data = new HashMap<>();

    @Override
    public MenuProduct save(MenuProduct entity) {
        data.put(entity.getSeq(), entity);
        return null;
    }

    @Override
    public Optional<MenuProduct> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<MenuProduct> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<MenuProduct> findAllByMenuId(Long menuId) {
        return data.values().stream()
                .filter(menuProduct -> menuProduct.getMenuId().equals(menuId))
                .collect(Collectors.toList());
    }
}
