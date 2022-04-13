package kitchenpos.domain;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMenuRepository implements MenuRepository {

    private Map<UUID, Menu> menus = new HashMap<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return menus.values().stream()
                .filter(it -> ids.contains(it.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values()
                .stream()
                .filter(menu -> menu.getMenuProducts()
                        .stream()
                        .anyMatch(
                                product -> Objects.equals(product.getProductId(), productId))
                ).collect(Collectors.toList());
    }

    @Override
    public Menu save(Menu menu) {
        menus.put(menu.getId(), menu);

        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return menus.values()
                .stream()
                .filter(it -> Objects.equals(it.getId(), id))
                .findAny();
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menus.values());
    }

}
