package kitchenpos.domain.testfixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MenuGroupFakeRepository implements MenuGroupRepository {

    private final InstancePocket<UUID, MenuGroup> instancePocket;

    public MenuGroupFakeRepository() {
        this.instancePocket = new InstancePocket<>();
    }

    @Override
    public void saveAll(List<MenuGroup> menuGroups) {
        this.instancePocket.saveAll(menuGroups);
    }

    @Override
    public Optional<MenuGroup> findById(UUID menuGroupId) {
        return instancePocket.findById(menuGroupId);
    }

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        return instancePocket.save(menuGroup);
    }

    @Override
    public List<MenuGroup> findAll() {
        return instancePocket.findAll();
    }
}
