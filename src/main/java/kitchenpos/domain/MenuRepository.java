package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuRepository {

    List<Menu> findAllByIdIn(List<UUID> ids);

    @Query("select m from Menu m join m.menuProducts mp where mp.product.id = :productId")
    List<Menu> findAllByProductId(@Param("productId") UUID productId);

    Menu save(Menu menu);

    Optional<Menu> findById(UUID id);

    List<Menu> findAll();
}

interface MenuRepositoryJpaRepository extends JpaRepository<Menu, UUID> {

}
