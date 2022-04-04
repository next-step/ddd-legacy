package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuGroupRepository {
    List<MenuGroup> findAll();

    MenuGroup save(MenuGroup menuGroup);

    Optional<MenuGroup> findById(UUID menuGroupId);
}
