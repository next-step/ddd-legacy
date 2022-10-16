package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderTableJpaRepository extends JpaRepository<OrderTable, UUID>, OrderTableRepository {
}
