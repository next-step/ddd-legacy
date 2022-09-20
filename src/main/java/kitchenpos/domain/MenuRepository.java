package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {

  Menu save(Menu menu);

  Optional<Menu> findById(UUID menuId);

  List<Menu> findAllByIdIn(List<UUID> ids);

  List<Menu> findAllByProductId(UUID productId);

  List<Menu> findAll();

  void deleteAll();
}
