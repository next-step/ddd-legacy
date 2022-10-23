package kitchenpos.menu.menugroup.infra;

import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaMenuGroupRepository extends MenuGroupRepository, JpaRepository<MenuGroup, UUID> {

}
