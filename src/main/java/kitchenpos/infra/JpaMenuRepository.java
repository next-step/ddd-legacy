package kitchenpos.infra;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaMenuRepository extends MenuRepository, JpaRepository<Menu, UUID> {
    @Nonnull
    @Override
    List<Menu> findAllByIdIn(@Nonnull List<UUID> ids);

    @Nonnull
    @Override
    @Query("select m from Menu m join m.menuProducts mp where mp.product.id = :productId")
    List<Menu> findAllByProductId(@Nonnull @Param("productId") UUID productId);
}
