package kitchenpos.menu.menu.domain;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {
    Menu save(Menu menu);

    List<Menu> findAllByIdIn(List<UUID> ids);

    Optional<Menu> findById(UUID id);

    List<Menu> findAll();

    @Query("select m from Menu m, MenuProduct mp where mp.product.id = :productId")
    List<Menu> findAllByProductId(@Param("productId") UUID productId);
}

