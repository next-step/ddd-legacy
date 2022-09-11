package kitchenpos;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {
    @Override
    public Optional<MenuGroup> findById(UUID menuGroupId) {
        return Optional.empty();
    }

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        return null;
    }

    @Override
    public List<MenuGroup> findAll() {
        return null;
    }
}
