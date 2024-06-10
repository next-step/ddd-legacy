package kitchenpos.fake.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

public class TestMenuRepository implements MenuRepository {
    private final ConcurrentHashMap<UUID, Menu> menus = new ConcurrentHashMap<>();

    @Override
    public Menu save(Menu menu) {
        menus.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menus.values());
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return new ArrayList<>(menus.values().stream().filter(it -> ids.contains(it)).toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values().stream().filter(it -> it.getMenuProducts().stream().anyMatch(mp -> mp.getProduct().getId().equals(productId))).toList();
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return Optional.ofNullable(menus.get(id));
    }
}
