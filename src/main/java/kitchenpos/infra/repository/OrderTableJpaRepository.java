package kitchenpos.infra.repository;

import java.util.UUID;
import kitchenpos.domain.OrderTable;
import org.springframework.data.jpa.repository.JpaRepository;

interface OrderTableJpaRepository extends JpaRepository<OrderTable, UUID> {
}
