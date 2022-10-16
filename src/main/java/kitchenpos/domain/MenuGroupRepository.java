package kitchenpos.domain;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuGroupRepository {
    MenuGroup save(MenuGroup menuGroup);

    Optional<MenuGroup> findById(UUID id);

    List<MenuGroup> findAll();
}
