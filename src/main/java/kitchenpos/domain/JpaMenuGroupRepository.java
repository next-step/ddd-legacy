package kitchenpos.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMenuGroupRepository extends
        MenuGroupRepository,
        JpaRepository<MenuGroup, UUID> {

}
