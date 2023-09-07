package kitchenpos.domain;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.query.Param;

public interface MenuGroupRepository {

    List<Menu> findAllByIdIn(List<UUID> ids);

    List<Menu> findAllByProductId(@Param("productId") UUID productId);

    MenuGroup save(MenuGroup entity);

    List<MenuGroup> findAll();

    Optional<MenuGroup> findById(UUID menuGroupId);
}
