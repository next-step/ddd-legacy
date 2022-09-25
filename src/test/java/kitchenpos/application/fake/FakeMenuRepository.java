package kitchenpos.application.fake;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FakeMenuRepository implements MenuRepository {

    private final Map<UUID, Menu> memoryMap = new HashMap<>();

    @Override
    public Menu save(Menu menu) {
        UUID uuid = UUID.randomUUID();
        menu.setId(uuid);

        memoryMap.put(uuid, menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID menuId) {
        return Optional.ofNullable(memoryMap.get(menuId));
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(memoryMap::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        List<Menu> menus = new ArrayList<>(memoryMap.values());

        return menus.stream()
                .filter(menu -> menu.containProduct(productId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(memoryMap.values());
    }
}
