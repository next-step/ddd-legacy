package kitchenpos.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

class FakeMenuRepository implements MenuRepository {

    private final Map<UUID, Menu> menus = new HashMap<>();

    @Override
    public Menu save(Menu menu) {
        this.menus.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        Menu result = this.menus.get(id);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Menu> findAll() {
        final Collection<Menu> results = this.menus.values();
        return List.copyOf(results);
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(this.menus::get)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return this.menus.values()
                .stream()
                .filter(menu -> menu.getMenuProducts()
                        .stream()
                        .anyMatch(menuProduct -> menuProduct.getProductId() == productId))
                .collect(Collectors.toUnmodifiableList());
    }
}
