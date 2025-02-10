package kitchenpos.application.fake;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryMenuRepository implements MenuRepository {
    private Map<UUID, Menu> menus = new ConcurrentHashMap<>();

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
        return new ArrayList<>(menus.values());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values()
                .stream()
                .filter(menu -> menu.getMenuProducts().stream().
                        anyMatch(menuProduct -> menuProduct.getProduct().getId().equals(productId)))
                .toList();
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return menus.values().stream()
                .filter(menu -> ids.contains(menu.getId()))
                .toList();
    }
}
