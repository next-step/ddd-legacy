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

    Map<UUID, Menu> database = new HashMap<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map((id) -> database.get(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return database.values().stream()
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
        database.put(menu.getId(), menu);
        return database.get(menu.getId());
    }

    @Override
    public Optional<Menu> findById(UUID menuId) {
        return Optional.ofNullable(database.get(menuId));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(database.values());
    }
}
