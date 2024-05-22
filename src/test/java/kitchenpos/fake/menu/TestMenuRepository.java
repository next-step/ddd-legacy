package kitchenpos.fake.menu;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

public class TestMenuRepository implements MenuRepository {
    @Override
    public Menu save(Menu menu) {
        return menu;
    }

    @Override
    public List<Menu> findAll() {
        return null;
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return null;
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return null;
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return Optional.empty();
    }
}
