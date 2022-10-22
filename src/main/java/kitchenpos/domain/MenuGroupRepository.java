package kitchenpos.domain;

import kitchenpos.menu.menugroup.domain.MenuGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuGroupRepository extends JpaRepository<MenuGroup, UUID> {
}
