package kitchenpos.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

public class InMemoryMenuRepository implements MenuRepository {

    private final Map<UUID, Menu> menus = new HashMap<>();

    @Override
    public Menu save(Menu entity) {
        menus.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return Optional.ofNullable(menus.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return List.copyOf(menus.values());
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
