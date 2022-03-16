package kitchenpos.domain;

import java.util.List;
import java.util.Optional;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuGroupRepository {

    MenuGroup save(MenuGroup menuGroup);

    List<MenuGroup> findAll();

    Optional<MenuGroup> findById(UUID menuGroupId);
}

interface JpaMenuGroupRepository extends MenuGroupRepository, JpaRepository<MenuGroup, UUID> {

}
