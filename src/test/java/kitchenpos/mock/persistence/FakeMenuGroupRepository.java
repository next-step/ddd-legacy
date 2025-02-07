package kitchenpos.mock.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

public class FakeMenuGroupRepository implements MenuGroupRepository {

    private final Map<UUID, MenuGroup> dataMap = new ConcurrentHashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        if (menuGroup.getId() == null) {
            menuGroup.setId(UUID.randomUUID());
        }
        UUID id = menuGroup.getId();
        dataMap.put(id, menuGroup);
        return menuGroup;
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return dataMap.values()
            .stream()
            .filter(menuGroup -> menuGroup.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(dataMap.values());
    }
}
