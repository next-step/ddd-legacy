package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {
    Menu save(Menu entity);
    Optional<Menu> findById(UUID uuid);
    List<Menu> findAllByIdIn(List<UUID> ids);
    List<Menu> findAll();
    List<Menu> findAllByProductId(UUID productId);
}
