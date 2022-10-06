package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {
    List<Menu> findAllByIdIn(List<UUID> ids);

    List<Menu> findAllByProductId(UUID productId);

    Menu save(Menu menu);

    Optional<Menu> findById(UUID menuId);

    List<Menu> findAll();
}
