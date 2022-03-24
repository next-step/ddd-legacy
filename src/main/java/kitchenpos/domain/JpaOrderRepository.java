package kitchenpos.domain;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaOrderRepository extends OrderRepository, JpaRepository<Order, UUID> {

}

