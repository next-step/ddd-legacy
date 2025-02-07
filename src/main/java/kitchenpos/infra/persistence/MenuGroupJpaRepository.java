package kitchenpos.infra.persistence;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuGroupJpaRepository extends MenuGroupRepository,
    JpaRepository<MenuGroup, UUID> {

}
