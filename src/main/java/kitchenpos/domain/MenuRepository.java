package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {

    Menu save(final Menu entity);

    Optional<Menu> findById(final UUID id);

    List<Menu> findAll();

    List<Menu> findAllByIdIn(final List<UUID> ids);

    List<Menu> findAllByProductId(final UUID productId);
}
