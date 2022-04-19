package kitchenpos.repository;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;

/**
 * <pre>
 * kitchenpos.repository
 *      InMemoryMenuGroupRepository
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-04-20 오전 1:37
 */

public class InMemoryMenuGroupRepository implements MenuGroupRepository {
    private Map<UUID, MenuGroup> menuGroups = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        menuGroups.put(menuGroup.getId(), menuGroup);

        return menuGroup;
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroups.values());
    }

    @Override
    public Optional<MenuGroup> findById(UUID menuGroupId) {
        return Optional.ofNullable(menuGroups.get(menuGroupId));
    }
}
