package kitchenpos.application.fake;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.assertj.core.util.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeMenuGroupRepository implements MenuGroupRepository {

    private final Map<UUID, MenuGroup> memoryMap = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        memoryMap.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }

    @Override
    public Optional<MenuGroup> findById(UUID menuGroupId) {
        return Optional.ofNullable(memoryMap.get(menuGroupId));
    }

    @Override
    public List<MenuGroup> findAll() {
        return Lists.newArrayList(memoryMap.values());
    }
}
