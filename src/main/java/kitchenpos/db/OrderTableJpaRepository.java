package kitchenpos.db;

import java.util.UUID;
import kitchenpos.domain.OrderTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTableJpaRepository extends JpaRepository<OrderTable, UUID> {

}
