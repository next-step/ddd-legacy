package kitchenpos.application.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

public class InMemoryMenuRepository implements MenuRepository {

  private final Map<UUID, Menu> maps = new ConcurrentHashMap<>();

  @Override
  public Menu save(Menu menu) {
    maps.put(menu.getId(), menu);
    return menu;
  }

  @Override
  public Optional<Menu> findById(UUID id) {
    return Optional.ofNullable(maps.get(id));
  }

  @Override
  public List<Menu> findAll() {
    return new ArrayList<>(maps.values());
  }

  @Override
  public List<Menu> findAllByIdIn(List<UUID> ids) {
    return ids.stream()
        .map(maps::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @Override
  public List<Menu> findAllByProductId(UUID productId) {
    return maps.values()
        .stream()
        .filter(menu -> menu.getMenuProducts().stream()
            .anyMatch(menuProduct -> menuProduct.getProduct().getId().equals(productId)))
        .collect(Collectors.toList());
  }
}
