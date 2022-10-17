package kitchenpos.application.fake;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;

public class FakeMenuGroupRepository implements MenuGroupRepository {

    private final Map<UUID, MenuGroup> menuGroupMap = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        UUID uuid = UUID.randomUUID();
        menuGroup.setId(uuid);
        menuGroupMap.put(uuid, menuGroup);
        return menuGroup;
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return Optional.ofNullable(menuGroupMap.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroupMap.values());
    }

}
