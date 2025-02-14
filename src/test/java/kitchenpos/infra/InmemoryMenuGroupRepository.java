package kitchenpos.infra;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;

public class InmemoryMenuGroupRepository implements MenuGroupRepository {

    private final IdGenerator idGenerator = UUID::randomUUID;
    private final Map<UUID, MenuGroup> storage = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        UUID id = idGenerator.random();
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