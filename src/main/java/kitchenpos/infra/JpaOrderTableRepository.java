package kitchenpos.infra;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaOrderTableRepository extends OrderTableRepository, JpaRepository<OrderTable, UUID> {

}
