package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.query.Param;

public interface MenuRepository {

    Menu save(Menu menu);

    Optional<Menu> findById(UUID menuId);

    List<Menu> findAll();

    List<Menu> findAllByIdIn(List<UUID> ids);

    List<Menu> findAllByProductId(@Param("productId") UUID productId);
}
