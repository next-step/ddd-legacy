package kitchenpos.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface MenuGroupRepository extends CrudRepository<MenuGroup, UUID> {
    List<MenuGroup> findAll();
}
