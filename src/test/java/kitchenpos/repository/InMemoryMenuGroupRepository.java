package kitchenpos.repository;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {
    private final BaseInMemoryDao<MenuGroup> dao = new BaseInMemoryDao<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        return dao.save(menuGroup);
    }

    @Override
    public List<MenuGroup> findAll() {
        return dao.findAll();
    }

    @Override
    public Optional<MenuGroup> findById(UUID menuGroupId) {
        return dao.findById(menuGroupId);
    }
}
