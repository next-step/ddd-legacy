package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaMenuRepository extends MenuRepository, JpaRepository<Menu, UUID> {

}

