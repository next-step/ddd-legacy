package kitchenpos.menu.product.supports;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import kitchenpos.dao.MenuProductDao;
import kitchenpos.model.MenuProduct;

public class MenuProductWithCollection implements MenuProductDao {

    private long sequence = 0;
    private final Map<Long, MenuProduct> entities;

    public MenuProductWithCollection(List<MenuProduct> entities) {
        this.entities = entities.stream()
                                .peek(e -> e.setSeq(++sequence))
                                .collect(Collectors.toMap(MenuProduct::getSeq,
                                                          Function.identity()));
    }

    @Override
    public MenuProduct save(MenuProduct entity) {
        if (entity.getSeq() == null) { entity.setSeq(++sequence); }
        entities.put(entity.getSeq(), entity);
        return entity;
    }

    @Override
    public Optional<MenuProduct> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<MenuProduct> findAll() {
        return null;
    }

    @Override
    public List<MenuProduct> findAllByMenuId(Long menuId) {
        return null;
    }
}
