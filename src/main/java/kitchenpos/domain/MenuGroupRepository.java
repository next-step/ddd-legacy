package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuGroupRepository {

  MenuGroup save(MenuGroup menuGroup);

  List<MenuGroup> findAll();

  Optional<MenuGroup> findById(UUID menuGroupId);

  void saveAll(List<MenuGroup> menuGroups);

  void deleteAll();
}
