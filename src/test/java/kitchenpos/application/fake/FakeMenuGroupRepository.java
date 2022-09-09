package kitchenpos.application.fake;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;

class FakeMenuGroupRepository implements MenuGroupRepository {
    private Map<UUID, MenuGroup> fakePersistence = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        return null;
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<MenuGroup> findAll() {
        return null;
    }
}
