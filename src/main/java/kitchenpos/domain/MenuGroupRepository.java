package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuGroupRepository {
    MenuGroup save(MenuGroup entity);

    List<MenuGroup> findAll();

    Optional<MenuGroup> findById(UUID id);

}
