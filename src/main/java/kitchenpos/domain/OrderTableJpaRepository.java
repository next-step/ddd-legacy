package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTableJpaRepository extends OrderTableRepository, JpaRepository<OrderTable, Long> {
}
