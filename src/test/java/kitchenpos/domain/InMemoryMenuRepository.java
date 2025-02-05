package kitchenpos.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryMenuRepository implements MenuRepository {

    private final Map<UUID, Menu> menus = new HashMap<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return menus.entrySet()
                    .stream().filter(entry -> ids.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .toList();
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values().stream()
                    .filter(menu -> menu.getMenuProducts().stream()
                                        .anyMatch(menuProduct -> menuProduct.getProduct().getId().equals(productId)))
                    .toList();
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
        return List.copyOf(menus.values());
    }
}
