package kitchenpos.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPAMenuGroupRepository extends JpaRepository<MenuGroup, UUID>, MenuGroupRepository {

}
