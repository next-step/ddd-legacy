package kitchenpos.application.fake;

import kitchenpos.domain.*;

import java.util.*;
import java.util.stream.Collectors;

public class FakeMenuRepository implements MenuRepository {

    private final Map<UUID, Menu> menuMap = new HashMap<>();

    @Override
    public Menu save(Menu menu) {
        UUID uuid = UUID.randomUUID();
        menu.setId(uuid);
        menuMap.put(uuid, menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID menuId) {
        return Optional.ofNullable(menuMap.get(menuId));
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(menuMap::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        List<Menu> menus = new ArrayList<>(menuMap.values());
        List<Menu> results = new ArrayList<>();
        menus.forEach(menu -> menu.getMenuProducts().stream()
                .filter(menuProduct -> menuProduct.getProductId().equals(productId))
                .map(menuProduct -> menu)
                .forEach(results::add));
        return results;
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menuMap.values());
    }

}
