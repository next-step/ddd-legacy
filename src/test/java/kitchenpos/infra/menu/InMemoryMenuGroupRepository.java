package kitchenpos.infra.menu;

import java.util.*;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {

  private final Map<UUID, MenuGroup> db = new HashMap<>();

  @Override
  public Optional<MenuGroup> findById(UUID uuid) {
    return Optional.ofNullable(db.get(uuid));
  }

  @Override
  public MenuGroup save(MenuGroup menuGroup) {
    db.put(menuGroup.getId(), menuGroup);
    return menuGroup;
  }

  @Override
  public List<MenuGroup> findAll() {
    return db.values().stream().toList();
  }
}
