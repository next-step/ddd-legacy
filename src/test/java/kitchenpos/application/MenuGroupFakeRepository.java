package kitchenpos.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

public class MenuGroupFakeRepository implements MenuGroupRepository {
  private final Map<UUID, MenuGroup> maps = new ConcurrentHashMap<>();

  @Override
  public MenuGroup save(MenuGroup menuGroup) {
    maps.put(menuGroup.getId(), menuGroup);
    return menuGroup;
  }

  @Override
  public List<MenuGroup> findAll() {
    return new ArrayList<>(maps.values());
  }

  @Override
  public Optional<MenuGroup> findById(UUID id) {
    return Optional.ofNullable(maps.get(id));
  }
}
