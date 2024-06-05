package kitchenpos.infra.menu;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeMenuGroupRepository implements MenuGroupRepository {
  private final HashMap<UUID, MenuGroup> menuGroups = new HashMap<>();

  @Override
  public MenuGroup save(MenuGroup menuGroup) {
    menuGroups.put(menuGroup.getId(), menuGroup);
    return menuGroup;
  }

  @Override
  public Optional<MenuGroup> findById(UUID id) {
    return Optional.ofNullable(menuGroups.get(id));
  }

  @Override
  public List<MenuGroup> findAll() {
    return menuGroups.values()
            .stream()
            .toList();
  }
}
