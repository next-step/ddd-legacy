package kitchenpos.infra;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaMenuRepository extends MenuRepository, JpaRepository<Menu, UUID> {

}
