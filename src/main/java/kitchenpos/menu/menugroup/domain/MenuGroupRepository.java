package kitchenpos.menu.menugroup.domain;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuGroupRepository {
    List<MenuGroup> findAll();

    Optional<MenuGroup> findById(UUID id);

    MenuGroup save(MenuGroup menuGroup);
}

