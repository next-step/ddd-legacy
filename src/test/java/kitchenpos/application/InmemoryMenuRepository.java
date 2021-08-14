package kitchenpos.application;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

public class InmemoryMenuRepository implements MenuRepository {

    private final Map<UUID, Menu> menus = new LinkedHashMap<>();

    @Override
    public Menu save(final Menu menu) {
        menus.put(menu.getId(), menu);
        return menu;
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
    public List<Menu> findAllByIdIn(final List<UUID> ids) {
        return menus.values()
            .stream()
            .filter(menu -> ids.contains(menu.getId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(final UUID productId) {
        return menus.values()
            .stream()
            .filter(menus -> menus.getMenuProducts()
                .stream()
                .anyMatch(menuProduct -> Objects.equals(menuProduct.getProductId(), productId)))
            .collect(Collectors.toList());
    }
}
