package kitchenpos.domain;

import java.util.*;

public class FakeMenuRepository implements MenuRepository {
    private Map<UUID, Menu> map = new HashMap<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return null;
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return null;
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
