package kitchenpos.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends OrderRepository, JpaRepository<Order, UUID> {

}
