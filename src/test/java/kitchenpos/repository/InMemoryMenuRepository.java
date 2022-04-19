package kitchenpos.repository;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMenuRepository implements MenuRepository {

    private Map<UUID, Menu> menus = new HashMap<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return menus.values()
                .stream()
                .filter(ids::contains)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values()
                .stream()
                .filter(menu -> menu.getMenuProducts()
                        .stream()
                        .anyMatch(product -> Objects.equals(product.getProductId(), productId))
                )
                .collect(Collectors.toList());
    }

    @Override
    public Menu save(Menu menu) {
        return menus.put(menu.getId(), menu);
    }

    @Override
    public Optional<Menu> findById(UUID uuid) {
        return Optional.ofNullable(menus.get(uuid));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menus.values());
    }
}