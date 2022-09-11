package kitchenpos;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class InMemoryMenuRepository implements MenuRepository {

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
        return null;
    }

    @Override
    public Optional<Menu> findById(UUID menuId) {
        return Optional.empty();
    }

    @Override
    public List<Menu> findAll() {
        return null;
    }
}
