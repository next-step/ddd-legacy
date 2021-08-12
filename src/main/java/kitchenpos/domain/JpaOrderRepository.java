package kitchenpos.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaOrderRepository extends OrderRepository, JpaRepository<Order, UUID> {

}
