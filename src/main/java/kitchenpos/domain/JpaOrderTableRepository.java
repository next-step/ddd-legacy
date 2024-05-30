package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaOrderTableRepository extends JpaRepository<OrderTable, UUID>, OrderTableRepository {
}
