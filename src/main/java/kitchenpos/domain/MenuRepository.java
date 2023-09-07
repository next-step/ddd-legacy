package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.query.Param;

public interface MenuRepository {

    List<Menu> findAllByIdIn(List<UUID> ids);

    Optional<Menu> findById(UUID id);

    List<Menu> findAllByProductId(UUID productId);

    Menu save(Menu entity);

    List<Menu> findAll();
}
