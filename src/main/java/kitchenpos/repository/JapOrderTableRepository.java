package kitchenpos.repository;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JapOrderTableRepository extends OrderTableRepository, JpaRepository<OrderTable, UUID> {
}
