package kitchenpos.infra.persistence;

import java.util.UUID;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTableJpaRepository extends OrderTableRepository,
    JpaRepository<OrderTable, UUID> {

}
