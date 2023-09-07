package kitchenpos.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

public class MenuGroupFakeRepository implements MenuGroupRepository {

    private final List<MenuGroup> menuGroups = new ArrayList<>();

    @Override
    public List<MenuGroup> findAll() {
        return Collections.unmodifiableList(menuGroups);
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return null;
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return null;
    }

    @Override
    public MenuGroup save(MenuGroup entity) {
        menuGroups.add(entity);
        return entity;
    }

    @Override
    public Optional<MenuGroup> findById(UUID uuid) {
        return menuGroups.stream()
            .filter(it -> uuid.equals(it.getId()))
            .findFirst();
    }
}
