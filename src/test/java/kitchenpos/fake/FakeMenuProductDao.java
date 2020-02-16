package kitchenpos.fake;

import kitchenpos.dao.MenuProductDao;
import kitchenpos.model.MenuProduct;

import java.util.*;
import java.util.stream.Collectors;

public class FakeMenuProductDao implements MenuProductDao {
    private Map<Long, MenuProduct> entities = new HashMap<>();

    @Override
    public MenuProduct save(MenuProduct entity) {
        entities.put(entity.getSeq(), entity);
        return entity;
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
        return entities.values()
                .stream()
                .filter(menuProduct -> menuProduct.getMenuId().equals(menuId))
                .collect(Collectors.toList());
    }
}
