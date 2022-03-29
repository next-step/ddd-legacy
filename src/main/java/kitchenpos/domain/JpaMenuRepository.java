package kitchenpos.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaMenuRepository extends MenuRepository, JpaRepository<Menu, UUID> {

}
