package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.*;
import java.util.stream.Collectors;

public class TestMenuRepository implements MenuRepository {
    private final Set<Menu> menus = new HashSet<>();

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.stream()
                .filter(menu -> menu.getMenuProducts().stream()
                        .anyMatch(menuProduct -> menuProduct.getProductId().equals(productId))
                ).collect(Collectors.toList());
    }

    @Override
    public Menu save(Menu menu) {
        if (menu.getId() == null) {
            menu.setId(UUID.randomUUID());
        }
        menus.add(menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID menuId) {
        return menus.stream()
                .filter(menu -> menu.getId().equals(menuId))
                .findAny();
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menus);
    }

    @Override
    public List<Menu> findAllById(List<UUID> ids) {
        return menus.stream()
                .filter(menu -> ids.contains(menu.getId()))
                .collect(Collectors.toList());
    }
}
