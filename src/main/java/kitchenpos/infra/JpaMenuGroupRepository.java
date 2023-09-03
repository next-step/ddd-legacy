package kitchenpos.infra;


import kitchenpos.domain.MenuGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaMenuGroupRepository extends JpaRepository<MenuGroup, UUID> {
}
