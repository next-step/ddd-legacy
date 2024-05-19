package kitchenpos.domain;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {
    Menu save(Menu entity);
    Optional<Menu> findById(UUID id);
    List<Menu> findAll();
    List<Menu> findAllByIdIn(List<UUID> ids);
    List<Menu> findAllByProductId(@Param("productId") UUID productId);
}
