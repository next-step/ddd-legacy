package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuGroupRepository {
    Optional<MenuGroup> findById(UUID uuid);
    MenuGroup save(MenuGroup menuGroup);
    List<MenuGroup> findAll();
}
