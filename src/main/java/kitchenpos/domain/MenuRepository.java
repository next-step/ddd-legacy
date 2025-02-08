package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
    List<Menu> findAllByIdIn(List<UUID> ids);

    @Query("select m from Menu m join m.menuProducts mp where mp.product.id = :productId")
    List<Menu> findAllByProductId(@Param("productId") UUID productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MenuProduct mp WHERE mp.product IN (SELECT p FROM Product p)")
    void deleteAllMenuProducts(); // 모든 menuProducts 직접 삭제
}
