package kitchenpos.application.fake.helper;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {

    private final Map<UUID, MenuGroup> elements = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup group) {
        elements.put(group.getId(), group);
        return group;
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return Optional.ofNullable(elements.get(id));
    }


    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(elements.values());
    }
}
