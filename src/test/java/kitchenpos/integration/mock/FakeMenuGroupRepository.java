package kitchenpos.integration.mock;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeMenuGroupRepository implements MenuGroupRepository {

    private static final Map<UUID, MenuGroup> STORE = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        return STORE.put(menuGroup.getId(), menuGroup);
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(STORE.values());
    }

    @Override
    public Optional<MenuGroup> findById(UUID uuid) {
        if (!STORE.containsKey(uuid)) {
            return Optional.empty();
        }

        return Optional.of(STORE.get(uuid));
    }

    public void clear() {
        STORE.clear();
    }
}
