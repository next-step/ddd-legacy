package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.stream.Collectors.toList;

public class InMemoryMenuRepository implements MenuRepository {

    private ConcurrentMap<UUID, Menu> menus = new ConcurrentHashMap<>();

    @Override
    public Menu save(Menu menu) {
        menus.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return Optional.ofNullable(menus.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menus.values());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values()
                .stream()
                .filter(menu -> menu.getMenuProducts()
                        .stream()
                        .anyMatch(menuProduct -> Objects.equals(productId, menuProduct.getProductId()))
                )
                .collect(toList());
    }

    @Override
    public List<Menu> findAllById(List<UUID> collect) {
        return collect.stream()
                .filter(id -> menus.containsKey(id))
                .map(id -> menus.get(id))
                .collect(toList());
    }
}
