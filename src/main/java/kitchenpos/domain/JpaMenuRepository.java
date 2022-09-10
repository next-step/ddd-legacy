package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaMenuRepository extends MenuRepository, JpaRepository<Menu, UUID> {
    @Query("select m from Menu m, MenuProduct mp where mp.product.id = :productId")
    List<Menu> findAllByProductId(@Param("productId") UUID productId);
}
