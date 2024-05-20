package kitchenpos.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeMenuGroupRepository implements MenuGroupRepository {

    private final HashMap<UUID, MenuGroup> inMemory = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        inMemory.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return Optional.ofNullable(inMemory.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(inMemory.values());
    }
}
