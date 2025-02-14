package kitchenpos.infra;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.util.*;

public class InmemoryMenuRepository implements MenuRepository {
    private final IdGenerator idGenerator = UUID::randomUUID;
    private final Map<UUID, Menu> store = new HashMap<>();

    @Override
    public Menu save(Menu menu) {
        UUID id = idGenerator.random();
        menu.setId(id);
        store.put(id, menu);
        return menu;
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return store.values().stream()
                .filter(menu -> ids.contains(menu.getId()))
                .toList();
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return store.values().stream()
                .filter(menu -> menu.getMenuProducts().stream()
                        .anyMatch(menuProduct -> menuProduct.getProduct().getId().equals(productId)))
                .toList();
    }

    @Override
    public Optional<Menu> findById(UUID menuId) {
        return Optional.ofNullable(store.get(menuId));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(store.values());
    }
}