package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {
    List<Menu> findAllByIdIn(List<UUID> ids);
    Optional<Menu> findById(UUID id);
    Menu save(Menu menu);
    List<Menu> findAll();
    List<Menu> findAllByProductId(UUID id);
}
