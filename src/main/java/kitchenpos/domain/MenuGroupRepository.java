package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuGroupRepository {

    MenuGroup save(final MenuGroup entity);

    Optional<MenuGroup> findById(final UUID id);
    
    List<MenuGroup> findAll();
}
