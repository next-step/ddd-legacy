package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuGroupRepository {
    Optional<MenuGroup> findById(UUID id);
    List<MenuGroup> findAll();
    MenuGroup save(MenuGroup menuGroup);
}
