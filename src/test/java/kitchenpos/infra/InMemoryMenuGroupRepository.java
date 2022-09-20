package kitchenpos.infra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

public class InMemoryMenuGroupRepository implements MenuGroupRepository {

  private final Map<UUID, MenuGroup> store = new HashMap<>();

  @Override
  public MenuGroup save(MenuGroup menuGroup) {
    store.put(menuGroup.getId(), menuGroup);
    return menuGroup;
  }

  @Override
  public List<MenuGroup> findAll() {
    return new ArrayList<>(store.values());
  }

  @Override
  public Optional<MenuGroup> findById(UUID menuGroupId) {
    return Optional.ofNullable(store.get(menuGroupId));
  }

  @Override
  public void deleteAll() {
    store.clear();
  }
}
