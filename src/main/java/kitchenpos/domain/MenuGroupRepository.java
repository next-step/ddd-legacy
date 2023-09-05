package kitchenpos.domain;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuGroupRepository {
    MenuGroup save(MenuGroup menuGroup);
    Optional<MenuGroup> findById(UUID uuid);
    List<MenuGroup> findAll();

}
