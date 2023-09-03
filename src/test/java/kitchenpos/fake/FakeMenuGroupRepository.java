package kitchenpos.fake;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeMenuGroupRepository implements MenuGroupRepository {
    private final List<MenuGroup> menuGroups = new ArrayList<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        this.menuGroups.add(menuGroup);
        return this.menuGroups.get(menuGroups.size()-1);
    }

    @Override
    public Optional<MenuGroup> findById(UUID uuid) {
        return this.menuGroups.stream()
                .filter(it -> it.getId().equals(uuid))
                .findFirst();
    }

    @Override
    public List<MenuGroup> findAll() {
        return this.menuGroups;
    }
}
