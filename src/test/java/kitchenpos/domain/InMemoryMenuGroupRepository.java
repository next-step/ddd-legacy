package kitchenpos.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {

    private final Map<UUID, MenuGroup> elements = new HashMap<>();


    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return Optional.ofNullable(elements.get(id));
    }

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        elements.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(elements.values());
    }
}
