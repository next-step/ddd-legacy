package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {
    Menu save(Menu menu);

    Optional<Menu> findById(UUID menuId);

    List<Menu> findAll();

    List<Menu> findAllById(List<UUID> menuIds);

    List<Menu> findAllByProductId(UUID productId);
}
