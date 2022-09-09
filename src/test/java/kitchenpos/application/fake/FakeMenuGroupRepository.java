package kitchenpos.application.fake;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeMenuGroupRepository implements MenuGroupRepository {
    private final Map<UUID, MenuGroup> fakePersistence = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        if (fakePersistence.containsKey(menuGroup.getId())) {
            throw new IllegalArgumentException("duplicate primary key");
        }
        fakePersistence.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return Optional.ofNullable(fakePersistence.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return fakePersistence.values()
                .stream()
                .collect(Collectors.toList());
    }
}
