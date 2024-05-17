package kitchenpos.infra;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.*;

public class InMemoryMenuRepository implements MenuRepository {

    private final Map<UUID, Menu> menus = new HashMap<>();

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
        return menus.values().stream().toList();
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return menus.values().stream()
            .filter(menu -> ids.contains(menu.getId()))
            .toList();
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values().stream()
            .filter(menu -> menu.getMenuProducts().stream()
                .anyMatch(menuProduct -> menuProduct.getProduct().getId().equals(productId)))
            .toList();
    }
}
