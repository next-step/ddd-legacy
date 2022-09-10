package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaMenuGroupRepository extends MenuGroupRepository, JpaRepository<MenuGroup, UUID> {

}
