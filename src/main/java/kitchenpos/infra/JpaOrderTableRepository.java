package kitchenpos.infra;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderTableRepository extends OrderTableRepository, JpaRepository<OrderTable, Long> {
}
