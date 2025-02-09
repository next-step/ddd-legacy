package kitchenpos.infra.repository;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuGroupJpaRepository extends MenuGroupRepository, JpaRepository<MenuGroup, UUID> {}
