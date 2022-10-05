package kitchenpos.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderTableRepository extends
    OrderTableRepository,
    JpaRepository<OrderTable, UUID> {

}
