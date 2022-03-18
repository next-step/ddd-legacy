package kitchenpos.domain;

import java.util.*;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {
    private Map<UUID, MenuGroup> menuGroups = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        UUID uuid = UUID.randomUUID();
        menuGroup.setId(uuid);
        menuGroups.put(uuid, menuGroup);

        return menuGroup;
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroups.values());
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return menuGroups.values()
                .stream()
                .filter(it -> Objects.equals(it.getId(), id))
                .findAny();
    }
}
