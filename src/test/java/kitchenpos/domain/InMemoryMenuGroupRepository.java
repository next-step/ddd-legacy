package kitchenpos.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {

    private final Map<UUID, MenuGroup> map = new LinkedHashMap<>();

    @Override
    public MenuGroup save(MenuGroup entity) {
        map.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(map.values());
    }
}
