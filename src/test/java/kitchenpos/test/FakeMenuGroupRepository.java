package kitchenpos.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

public class FakeMenuGroupRepository implements MenuGroupRepository {

    private final Map<UUID, MenuGroup> storage;

    public FakeMenuGroupRepository(Map<UUID, MenuGroup> storage) {
        this.storage = storage;
    }

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        UUID id = UUID.randomUUID();
        menuGroup.setId(id);
        storage.put(id, menuGroup);
        return menuGroup;
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<MenuGroup> findById(UUID menuGroupId) {
        return Optional.ofNullable(storage.get(menuGroupId));
    }
}
