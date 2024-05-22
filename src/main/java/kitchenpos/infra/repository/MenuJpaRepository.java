package kitchenpos.infra.repository;

import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface MenuJpaRepository extends JpaRepository<Menu, UUID> {
    List<Menu> findAllByIdIn(List<UUID> ids);

    @Query("select m from Menu m join m.menuProducts mp where mp.product.id = :productId")
    List<Menu> findAllByProductId(@Param("productId") UUID productId);
}

