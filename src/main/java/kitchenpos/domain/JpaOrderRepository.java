package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderRepository extends OrderRepository, JpaRepository<Order, Long>{
}
