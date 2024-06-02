package kitchenpos.infra.menu;

import java.util.*;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;

public class InMemoryMenuRepository implements MenuRepository {

  private final Map<UUID, Menu> db = new HashMap<>();

  @Override
  public List<Menu> findAllByIdIn(List<UUID> ids) {
    final List<Menu> menus = new ArrayList<>();
    for (UUID id : ids) {
      menus.add(db.get(id));
    }

    return menus;
  }

  @Override
  public List<Menu> findAllByProductId(UUID productId) {
    final List<Menu> findValue = new ArrayList<>();
    final List<Menu> values = db.values().stream().toList();
    for (Menu value : values) {
      for (MenuProduct menuProduct : value.getMenuProducts()) {
        if (menuProduct.getProductId().equals(productId)) {
          findValue.add(value);
        }
      }
    }

    return findValue;
  }

  @Override
  public Optional<Menu> findById(UUID uuid) {
    return Optional.ofNullable(db.get(uuid));
  }

  @Override
  public Menu save(Menu menu) {
    db.put(menu.getId(), menu);
    return menu;
  }

  @Override
  public List<Menu> findAll() {
    return db.values().stream().toList();
  }
}
