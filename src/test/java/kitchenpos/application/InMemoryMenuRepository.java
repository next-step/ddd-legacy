package kitchenpos.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

public class InMemoryMenuRepository implements MenuRepository {

    private final Map<UUID, Menu> menus = new HashMap<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return menus.values().stream()
            .filter(menu -> ids.contains(menu.getId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values().stream()
            .filter(
                menu -> menu.getMenuProducts()
                    .stream()
                    .anyMatch(menuProduct -> Objects.equals(menuProduct.getProductId(), productId))
            )
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Menu> findById(UUID menuId) {
        return Optional.ofNullable(menus.get(menuId));
    }

    @Override
    public Menu save(Menu menu) {
        menu.setId(UUID.randomUUID());
        menus.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menus.values());
    }
}
