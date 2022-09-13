package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderTableRepository extends OrderTableRepository , JpaRepository<OrderTable, Long> {
}
