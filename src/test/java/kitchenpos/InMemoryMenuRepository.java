package kitchenpos;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

public class InMemoryMenuRepository implements MenuRepository {

    private final HashMap<UUID, Menu> menus = new HashMap<>();


    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return null;
    }

    @Override
    public Menu save(Menu entity) {
        menus.put(UUID.randomUUID(), entity);
        return entity;
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return Optional.ofNullable(menus.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return null;
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return null;
    }
}