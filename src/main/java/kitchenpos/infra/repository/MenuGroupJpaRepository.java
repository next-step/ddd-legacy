package kitchenpos.infra.repository;

import kitchenpos.domain.MenuGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuGroupJpaRepository extends JpaRepository<MenuGroup, UUID> {
}
