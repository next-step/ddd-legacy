package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.*;

public class InMemoryMenuRepository implements MenuRepository {
    private final HashMap<UUID, Menu> menus = new HashMap<>();

    @Override
    public Menu save(final Menu menu) {
        menus.put(menu.getId(), menu);
        return menus.get(menu.getId());
    }

    @Override
    public Optional<Menu> findById(UUID menuId) {
        return Optional.ofNullable(menus.get(menuId));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menus.values());
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return menus.values().stream()
                .filter(menu -> ids.contains(menu.getId())).toList();
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values().stream()
                .filter(menu -> menu.getMenuProducts().stream()
                        .anyMatch(menuProduct -> menuProduct.getProduct().getId().equals(productId)))
                .toList();
    }
}
