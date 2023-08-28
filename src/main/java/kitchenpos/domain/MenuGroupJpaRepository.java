package kitchenpos.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuGroupJpaRepository
    extends JpaRepository<MenuGroup, UUID>, MenuGroupRepository {

}
