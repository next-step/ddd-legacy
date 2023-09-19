package kitchenpos.fake;

import kitchenpos.domain.*;

import java.util.*;
import java.util.stream.Collectors;

public class FakeMenuRepository implements MenuRepository {

    private Map<UUID, Menu> menus = new HashMap<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return menus.keySet().stream()
                .filter(ids::contains)
                .map(id -> menus.get(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values()
                .stream()
                .filter(menu -> hasProduct(menu.getMenuProducts(), productId))
                .collect(Collectors.toList());
    }

    private boolean hasProduct(List<MenuProduct> menuProducts, UUID productId) {
        return menuProducts.stream().anyMatch(it -> productId.equals(it.getProductId()));
    }

    @Override
    public Menu save(Menu menu) {
        menus.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return Optional.ofNullable(menus.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return menus.values()
                .stream()
                .collect(Collectors.toList());
    }
}
