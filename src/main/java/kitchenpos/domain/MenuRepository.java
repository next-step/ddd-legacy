package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.query.Param;

public interface MenuRepository {
    List<Menu> findAllByIdIn(List<UUID> ids);

    List<Menu> findAllByProductId(@Param("productId") UUID productId);

    Optional<Menu> findById(UUID id);

    Menu save(Menu menu);

    List<Menu> findAll();
}
