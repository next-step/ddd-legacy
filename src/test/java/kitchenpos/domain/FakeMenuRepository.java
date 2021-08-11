package kitchenpos.domain;

import java.util.*;
import java.util.stream.Collectors;

public class FakeMenuRepository implements MenuRepository {
    private final Map<UUID, Menu> menuMap = new HashMap<>();

    @Override
    public Menu save(final Menu menu) {
        menuMap.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(final UUID menuId) {
        return Optional.ofNullable(menuMap.get(menuId));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menuMap.values());
    }

    @Override
    public List<Menu> findAllById(final List<UUID> menuIds) {
        return menuIds.stream()
                .map(menuMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(final UUID productId) {
        return menuMap.values().stream()
                .filter(menu -> hasProduct(menu, productId))
                .collect(Collectors.toList());
    }

    private boolean hasProduct(final Menu menu, final UUID productId) {
        return menu.getMenuProducts().stream()
                .map(MenuProduct::getProduct)
                .map(Product::getId)
                .anyMatch(productId::equals);
    }
}
