package kitchenpos.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

public class MenuGroupFakeRepository implements MenuGroupRepository {

    private final Map<UUID, MenuGroup> menuGroups = new HashMap<>();

    @Override
    public MenuGroup save(final MenuGroup entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }

        menuGroups.put(entity.getId(), entity);

        return entity;
    }

    @Override
    public Optional<MenuGroup> findById(final UUID id) {
        return Optional.ofNullable(menuGroups.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return menuGroups.values()
            .stream()
            .collect(Collectors.toUnmodifiableList());
    }
}
