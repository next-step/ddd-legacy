package kitchenpos.domain;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakeMenuRepository implements MenuRepository {
    private Map<UUID, Menu> map = new HashMap<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return map.values()
                .stream()
                .filter(menu -> ids.contains(menu.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return map.values()
                .stream()
                .filter(menu -> menu.getMenuProducts().stream().anyMatch(menuProduct -> productId.equals(menuProduct.getProductId())))
                .collect(Collectors.toList());
    }

    @Override
    public Menu save(Menu menu) {
        map.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return null;
    }
}
