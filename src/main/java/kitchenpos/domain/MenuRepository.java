package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.query.Param;

public interface MenuRepository {

    List<Menu> findAllByProductId(@Param("productId") UUID productId);

    Menu save(Menu entity);

    Optional<Menu> findById(final UUID id);

    List<Menu> findAll();

    List<Menu> findAllByIdIn(List<UUID> ids);
}
