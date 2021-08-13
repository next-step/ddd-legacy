package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {
    Menu save(Menu menu);

    Optional<Menu> findById(UUID uuid);

    List<Menu> findAll();

    List<Menu> findAllById(List<UUID> uuids);

    List<Menu> findAllByProductId(UUID productId);
}
