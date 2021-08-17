package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface MenuGroupRepository {
    MenuGroup save(MenuGroup menuGroup);

    Optional<MenuGroup> findById(UUID uuid);

    List<MenuGroup> findAll();
}

interface JpaMenuGroupRepository extends MenuGroupRepository, JpaRepository<MenuGroup, UUID> {

}