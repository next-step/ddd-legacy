package kitchenpos.bo.mock;

import kitchenpos.dao.Interface.MenuProductDao;
import kitchenpos.model.MenuProduct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestMenuProductDao implements MenuProductDao {
    private static final Map<Long, MenuProduct> data = new HashMap<>();

    @Override
    public MenuProduct save(MenuProduct entity) {
        data.put(entity.getSeq(), entity);
        return entity;
    }

    @Override
    public Optional<MenuProduct> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<MenuProduct> findAllByMenuId(Long menuId) {
        return data.values()
                .stream()
                .filter(test -> test.getMenuId() == menuId)
                .collect(Collectors.toList());
    }
}
