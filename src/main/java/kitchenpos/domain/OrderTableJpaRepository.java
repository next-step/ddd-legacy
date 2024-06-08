package kitchenpos.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTableJpaRepository extends OrderTableRepository, JpaRepository<OrderTable, UUID> {

}
