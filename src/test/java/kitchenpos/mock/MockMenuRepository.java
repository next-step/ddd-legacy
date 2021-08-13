package kitchenpos.mock;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.*;
import java.util.stream.Collectors;

public class MockMenuRepository implements MenuRepository {
    private final Map<UUID, Menu> menuMap = new HashMap<>();

    @Override
    public Menu save(final Menu menu) {
        menuMap.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(final UUID uuid) {
        return Optional.ofNullable(menuMap.get(uuid));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menuMap.values());
    }

    @Override
    public List<Menu> findAllById(final List<UUID> uuids) {
        return menuMap.values()
                .stream()
                .filter(menu -> uuids.contains(menu.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(final UUID productId) {
        return menuMap.values()
                .stream()
                .filter(menu -> menu.getMenuProducts()
                        .stream()
                        .anyMatch(menuProduct -> Objects.equals(productId, menuProduct.getProduct().getId()))
                )
                .collect(Collectors.toList());
    }
}
