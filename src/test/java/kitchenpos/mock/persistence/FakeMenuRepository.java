package kitchenpos.mock.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;

public class FakeMenuRepository implements MenuRepository {

    private final Map<UUID, Menu> dataMap = new ConcurrentHashMap<>();

    @Override
    public Menu save(Menu menu) {
        if (menu.getId() == null) {
            menu.setId(UUID.randomUUID());
        }
        UUID id = menu.getId();
        dataMap.put(id, menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return dataMap.values()
            .stream()
            .filter(menu -> menu.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(dataMap.values());
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return dataMap.values().stream()
            .filter(menu -> ids.contains(menu.getId()))
            .toList();
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return dataMap.values().stream()
            .filter(menu -> Optional.ofNullable(menu.getMenuProducts())
                .orElse(Collections.emptyList())
                .stream()
                .map(MenuProduct::getProduct)
                .filter(Objects::nonNull)
                .anyMatch(product -> productId.equals(product.getId()))
            )
            .toList();
    }
}
