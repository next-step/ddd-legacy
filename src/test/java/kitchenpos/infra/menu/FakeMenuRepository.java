package kitchenpos.infra.menu;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeMenuRepository implements MenuRepository {

  private final HashMap<UUID, Menu> menus = new HashMap<>();

  @Override
  public Menu save(Menu menu) {
    menus.put(menu.getId(), menu);
    return menu;
  }

  @Override
  public Optional<Menu> findById(UUID id) {
    return Optional.ofNullable(menus.get(id));
  }

  @Override
  public List<Menu> findAll() {
    return menus.values()
            .stream()
            .toList();
  }

  @Override
  public List<Menu> findAllByIdIn(List<UUID> ids) {
    return ids.stream()
            .map(menus::get)
            .filter(Objects::nonNull)
            .toList();
  }

  @Override
  public List<Menu> findAllByProductId(UUID productId) {

    return menus.values()
            .stream()
            .filter(menu -> menu.getMenuProducts()
                    .stream()
                    .anyMatch(product -> product.getProductId() == productId))
            .collect(Collectors.toList());
  }
}
