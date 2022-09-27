package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

@RepositoryDefinition(domainClass = Menu.class, idClass = UUID.class)
public interface JpaMenuRepository extends MenuRepository, JpaSpecificationExecutor<Menu> {
    List<Menu> findAllByIdIn(List<UUID> ids);

    @Query("select m from Menu m, MenuProduct mp where mp.product.id = :productId")
    List<Menu> findAllByProductId(@Param("productId") UUID productId);
}
