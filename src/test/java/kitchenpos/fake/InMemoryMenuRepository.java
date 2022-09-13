package kitchenpos.fake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

public class InMemoryMenuRepository implements MenuRepository {

    private Map<UUID, Menu> menus = new HashMap<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map((id) -> menus.get(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values().stream()
                .filter(findByProductInMenu(productId))
                .collect(Collectors.toList());
    }

    private Predicate<Menu> findByProductInMenu(UUID productId) {
        return menu -> menu.getMenuProducts()
                .stream()
                .anyMatch((menuProduct -> menuProduct.getProductId().equals(productId)));
    }

    @Override
    public Menu save(Menu menu) {
        menu.setId(UUID.randomUUID());
        menus.put(menu.getId(), menu);
        return menus.get(menu.getId());
    }

    @Override
    public Optional<Menu> findById(UUID menuId) {
        return Optional.ofNullable(menus.get(menuId));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menus.values());
    }
}
