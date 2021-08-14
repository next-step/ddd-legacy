package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaMenuGroupRepository extends MenuGroupRepository, JpaRepository<MenuGroup, UUID> {

    @Override
    MenuGroup save(MenuGroup menuGroup);

    @Override
    Optional<MenuGroup> findById(UUID id);

    @Override
    List<MenuGroup> findAll();

}
