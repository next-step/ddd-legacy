package kitchenpos.domain;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaOrderTableRepository extends OrderTableRepository, JpaRepository<OrderTable, UUID> {

}

