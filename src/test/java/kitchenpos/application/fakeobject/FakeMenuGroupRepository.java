package kitchenpos.application.fakeobject;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;

public class FakeMenuGroupRepository implements MenuGroupRepository {
    private Map<UUID, MenuGroup> menuGroupMap;

    public FakeMenuGroupRepository() {
        this.menuGroupMap = new HashMap<>();

        for (int i = 1; i <= 5; i++) {
            UUID id = UUID.fromString("5e9879b7-6112-4791-a4ce-f22e94af875" + i);
            menuGroupMap.put(id, MenuGroup.of(id, "test" + i));
        }
    }

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        if (menuGroup.getId() != null) {
            if (menuGroupMap.containsKey(menuGroup.getId())) {
                menuGroupMap.put(menuGroup.getId(), menuGroup);
                return menuGroup;
            }
        }
        menuGroup.setId(UUID.randomUUID());
        menuGroupMap.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroupMap.values());
    }

    @Override
    public Optional<MenuGroup> findById(UUID menuGroupId) {
        if (menuGroupMap.containsKey(menuGroupId)) {
            return Optional.of(menuGroupMap.get(menuGroupId));
        }
        return Optional.empty();
    }
}
