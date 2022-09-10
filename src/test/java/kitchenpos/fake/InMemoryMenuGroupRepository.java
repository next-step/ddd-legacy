package kitchenpos.fake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {
    Map<UUID, MenuGroup> database = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        database.put(menuGroup.getId(), menuGroup);
        return database.get(menuGroup.getId());
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return Optional.of(database.get(id));
    }
}
