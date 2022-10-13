package kitchenpos.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;

public class InMemoryMenuRepository implements MenuRepository {

    private final Map<UUID, Menu> menus = new HashMap<>();

    @Override
    public Menu save(Menu menu) {
        menus.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID menuId) {
        return Optional.ofNullable(menus.get(menuId));
    }

    @Override
    public List<Menu> findAll() {
        return menus.values()
            .stream()
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> menuIds) {
        return menuIds.stream()
            .map(menus::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values()
            .stream()
            .filter(menu -> containProduct(menu, productId))
            .collect(Collectors.toUnmodifiableList());
    }

    private static boolean containProduct(Menu menu, UUID productId) {
        return menu.getMenuProducts()
            .stream()
            .map(MenuProduct::getProduct)
            .map(Product::getId)
            .anyMatch(productId::equals);
    }
}
