package kitchenpos.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

public class FakeMenuRepository implements MenuRepository {

    private final Map<UUID, Menu> storage;

    public FakeMenuRepository(Map<UUID, Menu> storage) {
        this.storage = storage;
    }

    @Override
    public Menu save(Menu menu) {
        UUID id = UUID.randomUUID();
        menu.setId(id);
        storage.put(id, menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID menuId) {
        return Optional.ofNullable(storage.get(menuId));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return storage.values().stream()
                .filter(menu -> ids.contains(menu.getId()))
                .toList();
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return storage.values().stream()
                .filter(menu -> menu.getMenuProducts().stream()
                        .anyMatch(menuProduct -> menuProduct.getProductId().equals(productId)))
                .toList();
    }

    public List<Menu> saveAll(List<Menu> menus) {
        for (Menu menu : menus) {
            UUID id = UUID.randomUUID();
            menu.setId(id);
            storage.put(id, menu);
        }
        return new ArrayList<>(storage.values());
    }
}
