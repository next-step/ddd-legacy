package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {
    List<Menu> findAllByIdIn(List<UUID> ids);

    List<Menu> findAllByProductId(UUID productId);
    Optional<Menu> findById(UUID uuid);
    Menu save(Menu menu);
    List<Menu> findAll();
}
