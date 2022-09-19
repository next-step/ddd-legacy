package kitchenpos.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryMenuRepository implements MenuRepository {

  private static Map<UUID, Menu> store = new HashMap<>();

  @Override
  public Menu save(Menu menu) {
    store.put(menu.getId(), menu);
    return menu;
  }

  @Override
  public Optional<Menu> findById(UUID menuId) {
    return Optional.ofNullable(store.get(menuId));
  }

  @Override
  public List<Menu> findAllByIdIn(List<UUID> ids) {
    return ids.stream()
        .map(it -> store.get(it))
        .collect(Collectors.toList());
  }

  @Override
  public List<Menu> findAllByProductId(UUID productId) {
    return store.values().stream()
        .filter(it -> it.getMenuProducts().stream()
            .anyMatch(product -> product.getProductId().equals(productId)))
        .collect(Collectors.toList());
  }

  @Override
  public List<Menu> findAll() {
    return new ArrayList<>(store.values());
  }
}
