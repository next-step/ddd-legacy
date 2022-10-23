package kitchenpos.menu.menu.application;


import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuRepository;

import java.util.*;

public class InMemoryMenuRepository implements MenuRepository {
    private final Map<UUID, Menu> menus = new HashMap<>();

    @Override
    public Menu save(final Menu menu) {
        menus.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return null;
    }

    @Override
    public Optional<Menu> findById(final UUID id) {
        return Optional.ofNullable(menus.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menus.values());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return null;
    }

}
