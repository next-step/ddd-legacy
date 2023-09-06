package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {

    Menu save(Menu menu);
    Optional<Menu> findById(UUID id);
    List<Menu> findAll();
    List<Menu> findAllByIdIn(List<UUID> ids);
    List<Menu> findAllByProductId(@Param("productId") UUID productId);
}

interface JpaMenuRepository extends MenuRepository, JpaRepository<Menu, UUID> {
    List<Menu> findAllByIdIn(List<UUID> ids);

    @Query("select m from Menu m, MenuProduct mp where mp.product.id = :productId")
    List<Menu> findAllByProductId(@Param("productId") UUID productId);
}
