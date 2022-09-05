package kitchenpos.integration.mock;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MemoryMenuRepository implements MenuRepository {

    private static final Map<UUID, Menu> STORE = new HashMap<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return STORE.values()
                .stream()
                .filter(it -> ids.contains(it.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return STORE.values()
                .stream()
                .filter(it -> containsProduct(it, productId))
                .collect(Collectors.toList());
    }

    private boolean containsProduct(Menu menu, UUID productId) {
        return menu.getMenuProducts()
                .stream()
                .anyMatch(it -> it.getProductId().equals(productId));
    }

    @Override
    public Menu save(Menu menu) {
        STORE.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID menuId) {
        if (!STORE.containsKey(menuId)) {
            return Optional.empty();
        }

        return Optional.of(STORE.get(menuId));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(STORE.values());
    }

    public void clear() {
        STORE.clear();
    }
}
