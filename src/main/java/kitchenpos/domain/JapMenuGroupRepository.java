package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JapMenuGroupRepository extends MenuGroupRepository, JpaRepository<MenuGroup, UUID> {
}
